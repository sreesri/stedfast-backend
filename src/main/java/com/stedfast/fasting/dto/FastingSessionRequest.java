package com.stedfast.fasting.dto;

import com.stedfast.fasting.models.FastingSession;
import lombok.Data;

@Data
public class FastingSessionRequest {
    private String scheduleId;
    private FastingSession.SessionType sessionType;
}
