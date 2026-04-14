package com.stedfast.fasting.dto;

import java.time.ZonedDateTime;

import com.stedfast.fasting.models.FastingSession;
import lombok.Data;

@Data
public class FastingSessionRequest {
    private String scheduleId;
    private FastingSession.SessionType sessionType;
    private ZonedDateTime startTime;

}
