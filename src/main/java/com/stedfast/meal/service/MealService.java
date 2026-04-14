package com.stedfast.meal.service;

import com.stedfast.exception.ResourceNotFoundException;
import com.stedfast.meal.dto.DishRequest;
import com.stedfast.meal.dto.MealLogRecordRequest;
import com.stedfast.meal.dto.MealLogDishRequest;
import com.stedfast.meal.models.Dish;
import com.stedfast.meal.models.MealLog;
import com.stedfast.meal.models.MealLogDish;
import com.stedfast.meal.models.UserIntakeSummary;
import com.stedfast.meal.repository.DishRepository;
import com.stedfast.meal.repository.MealLogRepository;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MealService {

    private final DishRepository dishRepository;
    private final MealLogRepository mealLogRepository;
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
    public MealLog saveMealLog(String userId, MealLogRecordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        MealLog mealLog = new MealLog();
        mealLog.setUser(user);
        mealLog.setMealTime(request.getMealTime());
        mealLog.setNotes(request.getNotes());

        List<MealLogDish> dishes = request.getDishes().stream().map(dRequest -> {
            MealLogDish mlDish = new MealLogDish();
            mlDish.setMealLog(mealLog);
            mlDish.setQuantity(dRequest.getQuantity());

            if (dRequest.getDishId() != null) {
                Dish template = dishRepository.findById(dRequest.getDishId())
                        .orElseThrow(() -> new ResourceNotFoundException("Dish template not found: " + dRequest.getDishId()));
                mlDish.setDish(template);
                mlDish.setName(template.getName());
                mlDish.setCalories(template.getCalories());
                mlDish.setProtein(template.getProtein());
                mlDish.setCarbs(template.getCarbs());
                mlDish.setFat(template.getFat());
            } else {
                mlDish.setName(dRequest.getName());
                mlDish.setCalories(dRequest.getCalories());
                mlDish.setProtein(dRequest.getProtein());
                mlDish.setCarbs(dRequest.getCarbs());
                mlDish.setFat(dRequest.getFat());
            }
            return mlDish;
        }).collect(Collectors.toList());

        mealLog.setDishes(dishes);
        updateMealLogTotals(mealLog);

        MealLog savedLog = mealLogRepository.save(mealLog);
        syncIntakeSummary(user, savedLog.getMealTime().toLocalDate());
        
        return savedLog;
    }

    @Transactional(readOnly = true)
    public List<MealLog> getMealLogsForDay(String userId, LocalDate date) {
        ZonedDateTime start = date.atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime end = date.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());
        return mealLogRepository.findAllByUserIdAndMealTimeBetween(userId, start, end);
    }

    @Transactional
    public void syncIntakeSummary(User user, LocalDate date) {
        ZonedDateTime start = date.atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime end = date.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());
        
        List<MealLog> dailyLogs = mealLogRepository.findAllByUserIdAndMealTimeBetween(user.getId(), start, end);
        
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
        
        for (MealLog log : dailyLogs) {
            totalCals += (log.getCalories() != null ? log.getCalories() : 0);
            totalProtein = totalProtein.add(log.getProtein() != null ? log.getProtein() : BigDecimal.ZERO);
            totalCarbs = totalCarbs.add(log.getCarbs() != null ? log.getCarbs() : BigDecimal.ZERO);
            totalFat = totalFat.add(log.getFat() != null ? log.getFat() : BigDecimal.ZERO);
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

    private BigDecimal safeMultiply(BigDecimal val, int qty) {
        return val != null ? val.multiply(BigDecimal.valueOf(qty)) : BigDecimal.ZERO;
    }
}
