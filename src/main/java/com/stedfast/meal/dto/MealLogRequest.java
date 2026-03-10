package com.stedfast.meal.dto;

import java.time.ZonedDateTime;

import com.stedfast.meal.models.MealType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MealLogRequest {
    private MealType mealType;
    private ZonedDateTime mealTime;
    private String dish;
    private Integer calories;
}
