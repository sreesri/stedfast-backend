package com.stedfast.weight.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class WeightLogRequest {
    private BigDecimal weight;
}
