package com.stedfast.meal.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stedfast.meal.dto.MealLogRequest;
import com.stedfast.meal.dto.MealLogResponse;
import com.stedfast.meal.models.MealType;
import com.stedfast.meal.service.MealLogService;
import com.stedfast.security.SecurityUser;

@WebMvcTest(value = MealLogController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
class MealLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MealLogService mealLogService;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getMealLogs_ShouldReturnMealLogsForDate() throws Exception {
        setAuthenticatedUser("user_123");

        ZonedDateTime mealTime = ZonedDateTime.parse("2026-03-10T08:00:00+05:30");
        MealLogResponse response = MealLogResponse.builder()
                .id("mealLog_123")
                .userId("user_123")
                .mealType(MealType.BREAKFAST)
                .mealTime(mealTime)
                .dish("Oats")
                .calories(350)
                .build();

        when(mealLogService.getMealLogs(eq("user_123"), any(ZonedDateTime.class))).thenReturn(List.of(response));

        mockMvc.perform(get("/api/meallog")
                .param("date", "2026-03-10T00:00:00+05:30")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("mealLog_123"))
                .andExpect(jsonPath("$[0].mealType").value("BREAKFAST"))
                .andExpect(jsonPath("$[0].dish").value("Oats"))
                .andExpect(jsonPath("$[0].calories").value(350));
    }

    @Test
    void saveMealLog_ShouldReturnCreated() throws Exception {
        setAuthenticatedUser("user_123");

        MealLogRequest request = new MealLogRequest();
        request.setMealType(MealType.DINNER);
        request.setMealTime(ZonedDateTime.parse("2026-03-10T20:00:00+05:30"));
        request.setDish("Grilled Paneer");
        request.setCalories(520);

        MealLogResponse response = MealLogResponse.builder()
                .id("mealLog_456")
                .userId("user_123")
                .mealType(request.getMealType())
                .mealTime(request.getMealTime())
                .dish(request.getDish())
                .calories(request.getCalories())
                .build();

        when(mealLogService.saveMealLog(eq("user_123"), any(MealLogRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/meallog")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("mealLog_456"))
                .andExpect(jsonPath("$.mealType").value("DINNER"))
                .andExpect(jsonPath("$.dish").value("Grilled Paneer"))
                .andExpect(jsonPath("$.calories").value(520));
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
