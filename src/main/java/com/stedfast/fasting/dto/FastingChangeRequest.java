package com.stedfast.fasting.dto;

import com.stedfast.fasting.models.FastingStatus;

import lombok.Data;

@Data
public class FastingChangeRequest {
    private FastingStatus status;
}
