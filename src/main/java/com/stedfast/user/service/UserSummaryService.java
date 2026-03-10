package com.stedfast.user.service;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stedfast.fasting.dto.CurrentFastingResponse;
import com.stedfast.fasting.service.FastingService;
import com.stedfast.meal.dto.MealLogResponse;
import com.stedfast.meal.service.MealLogService;
import com.stedfast.user.dto.UserSummaryResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserSummaryService {

    private final FastingService fastingService;
    private final MealLogService mealLogService;

    @Transactional(readOnly = true)
    public UserSummaryResponse getSummary(String userId, ZonedDateTime date) {
        CurrentFastingResponse fasting = fastingService.getCurrentFasting(userId);
        List<MealLogResponse> mealLogs = mealLogService.getMealLogs(userId, date);

        int totalCalories = mealLogs.stream()
                .map(MealLogResponse::getCalories)
                .filter(calories -> calories != null)
                .mapToInt(Integer::intValue)
                .sum();

        return UserSummaryResponse.builder()
                .fasting(fasting)
                .mealLogs(mealLogs)
                .totalCalories(totalCalories)
                .build();
    }
}

