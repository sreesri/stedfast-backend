package com.stedfast.meal.service;

import com.stedfast.meal.dto.DishRequest;
import com.stedfast.meal.dto.MealLogRecordRequest;
import com.stedfast.meal.models.Dish;
import com.stedfast.meal.models.MealLog;
import com.stedfast.meal.models.MealLogDish;
import com.stedfast.meal.repository.DishRepository;
import com.stedfast.meal.repository.MealLogRepository;
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
    private final UserRepository userRepository;

    @Transactional
    public Dish createDish(String userId, DishRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

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
                .orElseThrow(() -> new RuntimeException("Dish not found"));
        
        if (!dish.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        dishRepository.delete(dish);
    }

    @Transactional
    public MealLog saveMealLog(String userId, MealLogRecordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

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
                        .orElseThrow(() -> new RuntimeException("Dish template not found"));
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

        return mealLogRepository.save(mealLog);
    }

    @Transactional(readOnly = true)
    public List<MealLog> getMealLogsForDay(String userId, LocalDate date) {
        ZonedDateTime start = date.atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime end = date.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());
        return mealLogRepository.findAllByUserIdAndMealTimeBetween(userId, start, end);
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
