package com.stedfast.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import com.stedfast.fasting.dto.CurrentFastingResponse;
import com.stedfast.fasting.models.FastingStatus;
import com.stedfast.meal.dto.MealLogResponse;
import com.stedfast.meal.models.MealType;
import com.stedfast.security.SecurityUser;
import com.stedfast.user.dto.UserSummaryResponse;
import com.stedfast.user.service.UserSummaryService;

@WebMvcTest(value = UserSummaryController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
class UserSummaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserSummaryService userSummaryService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getSummary_ShouldReturnSummary() throws Exception {
        setAuthenticatedUser("user_123");

        CurrentFastingResponse fasting = CurrentFastingResponse.builder()
                .userId("user_123")
                .status(FastingStatus.EATING)
                .startTime(ZonedDateTime.parse("2026-03-10T09:00:00+05:30"))
                .build();

        List<MealLogResponse> mealLogs = List.of(
                MealLogResponse.builder()
                        .id("mealLog_1")
                        .userId("user_123")
                        .mealType(MealType.BREAKFAST)
                        .calories(300)
                        .build());

        UserSummaryResponse summary = UserSummaryResponse.builder()
                .fasting(fasting)
                .mealLogs(mealLogs)
                .totalCalories(300)
                .build();

        when(userSummaryService.getSummary(eq("user_123"), any(ZonedDateTime.class))).thenReturn(summary);

        mockMvc.perform(get("/api/user/summary")
                .param("date", "2026-03-10T00:00:00+05:30")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fasting.status").value("EATING"))
                .andExpect(jsonPath("$.fasting.startTime").exists())
                .andExpect(jsonPath("$.mealLogs[0].id").value("mealLog_1"))
                .andExpect(jsonPath("$.totalCalories").value(300));
    }

    private void setAuthenticatedUser(String userId) {
        SecurityUser securityUser = new SecurityUser("test@example.com", "password", List.of(), userId);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                securityUser,
                null,
                securityUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

