package com.stedfast.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.stedfast.fasting.dto.CurrentFastingResponse;
import com.stedfast.fasting.models.FastingStatus;
import com.stedfast.fasting.service.FastingService;
import com.stedfast.meal.dto.MealLogResponse;
import com.stedfast.meal.service.MealLogService;
import com.stedfast.user.dto.UserSummaryResponse;

@ExtendWith(MockitoExtension.class)
class UserSummaryServiceTest {

    @Mock
    private FastingService fastingService;

    @Mock
    private MealLogService mealLogService;

    @InjectMocks
    private UserSummaryService userSummaryService;

    @Test
    void getSummary_ShouldSumCalories() {
        String userId = "user_123";
        ZonedDateTime date = ZonedDateTime.parse("2026-03-10T00:00:00+05:30");

        when(fastingService.getCurrentFasting(userId)).thenReturn(CurrentFastingResponse.builder()
                .userId(userId)
                .status(FastingStatus.FASTING)
                .startTime(ZonedDateTime.parse("2026-03-09T19:00:00+05:30"))
                .build());

        when(mealLogService.getMealLogs(userId, date)).thenReturn(List.of(
                MealLogResponse.builder().id("m1").userId(userId).calories(200).build(),
                MealLogResponse.builder().id("m2").userId(userId).calories(null).build(),
                MealLogResponse.builder().id("m3").userId(userId).calories(350).build()));

        UserSummaryResponse summary = userSummaryService.getSummary(userId, date);

        assertEquals(550, summary.getTotalCalories());
        assertEquals(3, summary.getMealLogs().size());
        assertEquals(FastingStatus.FASTING, summary.getFasting().getStatus());
    }
}

