package com.stedfast.meal.service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stedfast.meal.dto.MealLogRequest;
import com.stedfast.meal.dto.MealLogResponse;
import com.stedfast.meal.models.MealLog;
import com.stedfast.meal.repository.MealLogRepository;
import com.stedfast.user.models.User;
import com.stedfast.user.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MealLogService {

    private final MealLogRepository mealLogRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<MealLogResponse> getMealLogs(String userId, ZonedDateTime date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        ZonedDateTime start = date.truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime end = start.plusDays(1);

        return mealLogRepository.findAllByUser_IdAndMealTimeBetween(user.getId(), start, end)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MealLogResponse saveMealLog(String userId, MealLogRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        MealLog mealLog = new MealLog();
        mealLog.setUser(user);
        mealLog.setMealType(request.getMealType());
        mealLog.setMealTime(request.getMealTime());
        mealLog.setDish(request.getDish());
        mealLog.setCalories(request.getCalories());

        return toResponse(mealLogRepository.save(mealLog));
    }

    @Transactional
    public void deleteMealLog(String userId, String mealLogId) {
        MealLog mealLog = mealLogRepository.findByIdAndUser_Id(mealLogId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Meal log not found or unauthorized"));
        mealLogRepository.delete(mealLog);
    }

    private MealLogResponse toResponse(MealLog mealLog) {
        return MealLogResponse.builder()
                .id(mealLog.getId())
                .userId(mealLog.getUser().getId())
                .mealType(mealLog.getMealType())
                .mealTime(mealLog.getMealTime())
                .dish(mealLog.getDish())
                .calories(mealLog.getCalories())
                .build();
    }
}
