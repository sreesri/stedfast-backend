package com.stedfast.health.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BodyStatsRequest {
    private LocalDate loggedDate;
    private BigDecimal heightCm;
    private BigDecimal weightKg;
    private BigDecimal waistCm;
    private BigDecimal chestCm;
    private BigDecimal hipsCm;
    private BigDecimal bodyFatPct;
}
