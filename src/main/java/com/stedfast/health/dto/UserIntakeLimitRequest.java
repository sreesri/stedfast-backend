package com.stedfast.health.dto;

import lombok.Data;

@Data
public class UserIntakeLimitRequest {
    private Integer calorieLimit;
    private Integer proteinLimit;
    private Integer carbsLimit;
    private Integer fatLimit;
}
