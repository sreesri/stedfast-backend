package com.stedfast.fasting.dto;

import lombok.Data;

@Data
public class FastingScheduleRequest {
    private Integer fastingHours;
    private Integer eatingHours;
    private String label;
}
