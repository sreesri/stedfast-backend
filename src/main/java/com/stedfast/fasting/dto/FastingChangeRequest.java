package com.stedfast.fasting.dto;

import com.stedfast.fasting.models.FastingStatus;

import java.time.ZonedDateTime;

import lombok.Data;

@Data
public class FastingChangeRequest {
    private FastingStatus status;
    private ZonedDateTime startTime;
}
