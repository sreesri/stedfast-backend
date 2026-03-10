package com.stedfast.meal.dto;

import java.time.ZonedDateTime;

import com.stedfast.meal.models.MealType;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MealLogResponse {
    private String id;
    private String userId;
    private MealType mealType;
    private ZonedDateTime mealTime;
    private String dish;
    private Integer calories;
}
