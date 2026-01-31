package com.stedfast.weight;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class WeightLogRequest {
    private Long userId;
    private BigDecimal weight;
}
