package com.stedfast.fasting.dto;

import com.stedfast.fasting.models.FastingStatus;
import lombok.Builder;
import lombok.Data;
import java.time.ZonedDateTime;

@Data
@Builder
public class CurrentFastingResponse {
    private String userId;
    private FastingStatus status;
    private ZonedDateTime startTime;
}