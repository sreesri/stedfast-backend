package com.stedfast.meal.dto;

import lombok.Data;
import java.util.List;
import java.time.ZonedDateTime;

@Data
public class MealLogRecordRequest {
    private ZonedDateTime mealTime;
    private String notes;
    private List<MealLogDishRequest> dishes;
}

@Data
class MealLogDishRequest {
    private String dishId;
    private String name;
    private Integer calories;
    private java.math.BigDecimal protein;
    private java.math.BigDecimal carbs;
    private java.math.BigDecimal fat;
    private Integer quantity;
}
