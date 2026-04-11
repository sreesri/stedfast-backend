package com.stedfast.meal.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DishRequest {
    private String name;
    private Integer calories;
    private BigDecimal protein;
    private BigDecimal carbs;
    private BigDecimal fat;
}
