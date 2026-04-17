package com.stedfast.meal.dto;

import lombok.Data;
import java.util.List;

@Data
public class MealRequest {
    private String name;
    private String notes;
    private List<MealDishRequest> dishes;
}
