package com.stedfast.user.dto;

import java.util.List;

import com.stedfast.fasting.dto.CurrentFastingResponse;
import com.stedfast.meal.dto.MealLogResponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSummaryResponse {
    private CurrentFastingResponse fasting;
    private List<MealLogResponse> mealLogs;
    private Integer totalCalories;
}

