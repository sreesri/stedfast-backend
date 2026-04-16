package com.stedfast.meal.service;

import com.stedfast.exception.ResourceNotFoundException;
import com.stedfast.meal.dto.DishRequest;
import com.stedfast.meal.dto.MealLogRecordRequest;
import com.stedfast.meal.models.Dish;
import com.stedfast.meal.models.Meal;
import com.stedfast.meal.models.MealDish;
import com.stedfast.meal.models.UserIntakeSummary;
import com.stedfast.meal.repository.DishRepository;
import com.stedfast.meal.repository.MealRepository;
import com.stedfast.meal.repository.UserIntakeSummaryRepository;
import com.stedfast.health.models.UserIntakeLimit;
import com.stedfast.health.repository.UserIntakeLimitRepository;
import com.stedfast.user.models.User;
import com.stedfast.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MealService {

    private final DishRepository dishRepository;
    private final MealRepository mealRepository;
    private final UserIntakeSummaryRepository intakeSummaryRepository;
    private final UserIntakeLimitRepository intakeLimitRepository;
    private final UserRepository userRepository;

    @Transactional
    public Dish createDish(String userId, DishRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Dish dish = new Dish();
        dish.setUser(user);
        dish.setName(request.getName());
        dish.setCalories(request.getCalories());
        dish.setProtein(request.getProtein());
        dish.setCarbs(request.getCarbs());
        dish.setFat(request.getFat());

        return dishRepository.save(dish);
    }

    public List<Dish> getUserDishes(String userId) {
        return dishRepository.findAllByUserId(userId);
    }

    @Transactional
    public void deleteDish(String userId, String dishId) {
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found: " + dishId));

        if (!dish.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Unauthorized access to dish: " + dishId);
        }

        dishRepository.delete(dish);
    }

    @Transactional
    public Meal createMeal(String userId, MealLogRecordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Meal meal = new Meal();
        meal.setUser(user);
        meal.setNotes(request.getNotes());
        meal.setCreatedAt(request.getMealTime() != null ? request.getMealTime() : ZonedDateTime.now());

        List<MealDish> dishes = (request.getDishes()).stream().map(dRequest -> {
            MealDish mealDish = new MealDish();
            mealDish.setMeal(meal);
            mealDish.setQuantity(dRequest.getQuantity() != null ? dRequest.getQuantity() : 1);
            mealDish.setCreatedAt(meal.getCreatedAt());

            if (dRequest.getDishId() != null) {
                Dish template = dishRepository.findById(dRequest.getDishId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Dish template not found: " + dRequest.getDishId()));
                if (!template.getUser().getId().equals(userId)) {
                    throw new ResourceNotFoundException("Dish template not found: " + dRequest.getDishId());
                }
                mealDish.setDish(template);
                mealDish.setName(template.getName());
                mealDish.setCalories(template.getCalories());
                mealDish.setProtein(template.getProtein());
                mealDish.setCarbs(template.getCarbs());
                mealDish.setFat(template.getFat());
            } else {
                mealDish.setName(dRequest.getName());
                mealDish.setCalories(dRequest.getCalories());
                mealDish.setProtein(dRequest.getProtein());
                mealDish.setCarbs(dRequest.getCarbs());
                mealDish.setFat(dRequest.getFat());
            }
            return mealDish;
        }).collect(Collectors.toList());

        meal.setDishes(dishes);
        updateMealTotals(meal);

        Meal savedMeal = mealRepository.save(meal);
        syncIntakeSummary(user, savedMeal.getCreatedAt().toLocalDate());

        return savedMeal;
    }

    @Transactional(readOnly = true)
    public List<Meal> getMealsForDay(String userId, LocalDate date) {
        ZonedDateTime start = date.atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime end = date.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());
        return mealRepository.findAllByUser_IdAndCreatedAtBetweenOrderByCreatedAtDesc(userId, start, end);
    }

    @Transactional(readOnly = true)
    public Meal getMeal(String userId, String mealId) {
        return mealRepository.findByIdAndUser_Id(mealId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal not found: " + mealId));
    }

    @Transactional
    public void deleteMeal(String userId, String mealId) {
        Meal meal = mealRepository.findByIdAndUser_Id(mealId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal not found: " + mealId));

        LocalDate mealDate = meal.getCreatedAt().toLocalDate();
        User user = meal.getUser();

        mealRepository.delete(meal);
        syncIntakeSummary(user, mealDate);
    }

    @Transactional
    public void syncIntakeSummary(User user, LocalDate date) {
        ZonedDateTime start = date.atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime end = date.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());

        List<Meal> dailyMeals = mealRepository.findAllByUser_IdAndCreatedAtBetweenOrderByCreatedAtDesc(user.getId(),
                start, end);

        UserIntakeSummary summary = intakeSummaryRepository.findByUserIdAndLoggedDateBetween(user.getId(), start, end)
                .orElse(new UserIntakeSummary());

        if (summary.getId() == null) {
            summary.setUser(user);
            summary.setLoggedDate(start);

            // Set limits from current settings
            UserIntakeLimit limits = intakeLimitRepository.findByUserId(user.getId()).orElse(null);
            summary.setCalorieLimit(limits != null ? limits.getCalorieLimit() : 2000);
            summary.setProteinLimit(limits != null ? limits.getProteinLimit() : 150);
            summary.setCarbsLimit(limits != null ? limits.getCarbsLimit() : 250);
            summary.setFatLimit(limits != null ? limits.getFatLimit() : 70);
        }

        int totalCals = 0;
        BigDecimal totalProtein = BigDecimal.ZERO;
        BigDecimal totalCarbs = BigDecimal.ZERO;
        BigDecimal totalFat = BigDecimal.ZERO;

        for (Meal meal : dailyMeals) {
            totalCals += (meal.getCalories() != null ? meal.getCalories() : 0);
            totalProtein = totalProtein.add(meal.getProtein() != null ? meal.getProtein() : BigDecimal.ZERO);
            totalCarbs = totalCarbs.add(meal.getCarbs() != null ? meal.getCarbs() : BigDecimal.ZERO);
            totalFat = totalFat.add(meal.getFat() != null ? meal.getFat() : BigDecimal.ZERO);
        }

        summary.setConsumedCalories(totalCals);
        summary.setConsumedProtein(totalProtein.intValue());
        summary.setConsumedCarbs(totalCarbs.intValue());
        summary.setConsumedFat(totalFat.intValue());

        intakeSummaryRepository.save(summary);
    }

    public List<UserIntakeSummary> getIntakeSummaries(String userId, LocalDate startDate, LocalDate endDate) {
        ZonedDateTime start = startDate.atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime end = endDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());
        return intakeSummaryRepository.findAllByUserIdAndLoggedDateBetweenOrderByLoggedDateAsc(userId, start, end);
    }

    private void updateMealTotals(Meal meal) {
        int totalCalories = 0;
        BigDecimal totalProtein = BigDecimal.ZERO;
        BigDecimal totalCarbs = BigDecimal.ZERO;
        BigDecimal totalFat = BigDecimal.ZERO;

        for (MealDish dish : meal.getDishes()) {
            int qty = dish.getQuantity() != null ? dish.getQuantity() : 1;
            totalCalories += (dish.getCalories() != null ? dish.getCalories() : 0) * qty;
            totalProtein = totalProtein.add(safeMultiply(dish.getProtein(), qty));
            totalCarbs = totalCarbs.add(safeMultiply(dish.getCarbs(), qty));
            totalFat = totalFat.add(safeMultiply(dish.getFat(), qty));
        }

        meal.setCalories(totalCalories);
        meal.setProtein(totalProtein);
        meal.setCarbs(totalCarbs);
        meal.setFat(totalFat);
    }

    private BigDecimal safeMultiply(BigDecimal val, int qty) {
        return val != null ? val.multiply(BigDecimal.valueOf(qty)) : BigDecimal.ZERO;
    }
}
