package com.stedfast.meal.service;

import com.stedfast.exception.ResourceNotFoundException;
import com.stedfast.meal.dto.DishRequest;
import com.stedfast.meal.dto.MealLogRecordRequest;
import com.stedfast.meal.dto.MealRequest;
import com.stedfast.meal.models.Dish;
import com.stedfast.meal.models.Meal;
import com.stedfast.meal.models.MealDish;
import com.stedfast.meal.models.MealLog;
import com.stedfast.meal.models.MealLogDish;
import com.stedfast.meal.models.UserIntakeSummary;
import com.stedfast.meal.repository.DishRepository;
import com.stedfast.meal.repository.MealLogRepository;
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
    private final MealLogRepository mealLogRepository;
    private final UserIntakeSummaryRepository intakeSummaryRepository;
    private final UserIntakeLimitRepository intakeLimitRepository;
    private final UserRepository userRepository;

    // ----- Dish Templates -----
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

    // ----- Meal Templates -----
    @Transactional
    public Meal createMealTemplate(String userId, MealRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Meal meal = new Meal();
        meal.setUser(user);
        meal.setName(request.getName());
        meal.setNotes(request.getNotes());
        meal.setCreatedAt(ZonedDateTime.now());

        if (request.getDishes() != null) {
            List<MealDish> dishes = request.getDishes().stream().map(dRequest -> {
                MealDish mealDish = new MealDish();
                mealDish.setMeal(meal);
                mealDish.setQuantity(dRequest.getQuantity() != null ? dRequest.getQuantity() : 1);
                mealDish.setCreatedAt(meal.getCreatedAt());

                if (dRequest.getDishId() != null) {
                    Dish template = dishRepository.findById(dRequest.getDishId())
                            .orElseThrow(() -> new ResourceNotFoundException("Dish template not found: " + dRequest.getDishId()));
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
        } else {
            meal.setDishes(Collections.emptyList());
        }

        updateMealTemplateTotals(meal);
        return mealRepository.save(meal);
    }

    @Transactional(readOnly = true)
    public List<Meal> getUserMealTemplates(String userId) {
        return mealRepository.findAllByUser_Id(userId);
    }

    @Transactional(readOnly = true)
    public Meal getMealTemplate(String userId, String mealId) {
        return mealRepository.findByIdAndUser_Id(mealId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal template not found: " + mealId));
    }

    @Transactional
    public void deleteMealTemplate(String userId, String mealId) {
        Meal meal = mealRepository.findByIdAndUser_Id(mealId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal template not found: " + mealId));
        mealRepository.delete(meal);
    }

    private void updateMealTemplateTotals(Meal meal) {
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


    // ----- Meal Logs -----
    @Transactional
    public MealLog createMealLog(String userId, MealLogRecordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        MealLog mealLog = new MealLog();
        mealLog.setUser(user);
        mealLog.setNotes(request.getNotes());
        mealLog.setMealTime(request.getMealTime() != null ? request.getMealTime() : ZonedDateTime.now());

        if (request.getDishes() != null) {
            List<MealLogDish> dishes = request.getDishes().stream().map(dRequest -> {
                MealLogDish mealLogDish = new MealLogDish();
                mealLogDish.setMealLog(mealLog);
                mealLogDish.setQuantity(dRequest.getQuantity() != null ? dRequest.getQuantity() : 1);
                mealLogDish.setCreatedAt(mealLog.getMealTime());

                if (dRequest.getDishId() != null) {
                    Dish template = dishRepository.findById(dRequest.getDishId())
                            .orElseThrow(() -> new ResourceNotFoundException("Dish template not found: " + dRequest.getDishId()));
                    if (!template.getUser().getId().equals(userId)) {
                        throw new ResourceNotFoundException("Dish template not found: " + dRequest.getDishId());
                    }
                    mealLogDish.setDish(template);
                    mealLogDish.setName(template.getName());
                    mealLogDish.setCalories(template.getCalories());
                    mealLogDish.setProtein(template.getProtein());
                    mealLogDish.setCarbs(template.getCarbs());
                    mealLogDish.setFat(template.getFat());
                } else {
                    mealLogDish.setName(dRequest.getName());
                    mealLogDish.setCalories(dRequest.getCalories());
                    mealLogDish.setProtein(dRequest.getProtein());
                    mealLogDish.setCarbs(dRequest.getCarbs());
                    mealLogDish.setFat(dRequest.getFat());
                }
                return mealLogDish;
            }).collect(Collectors.toList());

            mealLog.setDishes(dishes);
        } else {
            mealLog.setDishes(Collections.emptyList());
        }

        updateMealLogTotals(mealLog);

        MealLog savedMealLog = mealLogRepository.save(mealLog);
        syncIntakeSummary(user, savedMealLog.getMealTime().toLocalDate());

        return savedMealLog;
    }

    @Transactional(readOnly = true)
    public List<MealLog> getMealLogsForDay(String userId, LocalDate date) {
        ZonedDateTime start = date.atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime end = date.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());
        return mealLogRepository.findAllByUserIdAndMealTimeBetween(userId, start, end);
    }

    @Transactional(readOnly = true)
    public MealLog getMealLog(String userId, String mealLogId) {
        return mealLogRepository.findByIdAndUserId(mealLogId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal log not found: " + mealLogId));
    }

    @Transactional
    public void deleteMealLog(String userId, String mealLogId) {
        MealLog mealLog = mealLogRepository.findByIdAndUserId(mealLogId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal log not found: " + mealLogId));

        LocalDate mealDate = mealLog.getMealTime().toLocalDate();
        User user = mealLog.getUser();

        mealLogRepository.delete(mealLog);
        syncIntakeSummary(user, mealDate);
    }

    private void updateMealLogTotals(MealLog mealLog) {
        int totalCalories = 0;
        BigDecimal totalProtein = BigDecimal.ZERO;
        BigDecimal totalCarbs = BigDecimal.ZERO;
        BigDecimal totalFat = BigDecimal.ZERO;

        for (MealLogDish dish : mealLog.getDishes()) {
            int qty = dish.getQuantity() != null ? dish.getQuantity() : 1;
            totalCalories += (dish.getCalories() != null ? dish.getCalories() : 0) * qty;
            totalProtein = totalProtein.add(safeMultiply(dish.getProtein(), qty));
            totalCarbs = totalCarbs.add(safeMultiply(dish.getCarbs(), qty));
            totalFat = totalFat.add(safeMultiply(dish.getFat(), qty));
        }

        mealLog.setCalories(totalCalories);
        mealLog.setProtein(totalProtein);
        mealLog.setCarbs(totalCarbs);
        mealLog.setFat(totalFat);
    }


    // ----- Intake Summary -----
    @Transactional
    public void syncIntakeSummary(User user, LocalDate date) {
        ZonedDateTime start = date.atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime end = date.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());

        List<MealLog> dailyLog = mealLogRepository.findAllByUserIdAndMealTimeBetween(user.getId(), start, end);

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

        for (MealLog mealLog : dailyLog) {
            totalCals += (mealLog.getCalories() != null ? mealLog.getCalories() : 0);
            totalProtein = totalProtein.add(mealLog.getProtein() != null ? mealLog.getProtein() : BigDecimal.ZERO);
            totalCarbs = totalCarbs.add(mealLog.getCarbs() != null ? mealLog.getCarbs() : BigDecimal.ZERO);
            totalFat = totalFat.add(mealLog.getFat() != null ? mealLog.getFat() : BigDecimal.ZERO);
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

    private BigDecimal safeMultiply(BigDecimal val, int qty) {
        return val != null ? val.multiply(BigDecimal.valueOf(qty)) : BigDecimal.ZERO;
    }
}
