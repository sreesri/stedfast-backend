package com.stedfast.weight.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stedfast.user.models.User;
import com.stedfast.weight.dto.WeightLogRequest;
import com.stedfast.weight.models.WeightLog;
import com.stedfast.weight.service.WeightLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = WeightLogController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
class WeightLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeightLogService weightLogService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void logWeight_ShouldReturnCreated() throws Exception {
        WeightLogRequest request = new WeightLogRequest();
        request.setWeight(new BigDecimal("75.50"));

        WeightLog createdLog = new WeightLog();
        createdLog.setId("weight_123");
        createdLog.setWeight(request.getWeight());
        createdLog.setLoggedTime(LocalDateTime.now());
        User user = new User();
        user.setId("user_123");
        createdLog.setUser(user);

        when(weightLogService.logWeight(any(), any(WeightLogRequest.class))).thenReturn(createdLog);

        mockMvc.perform(post("/api/weights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("weight_123"))
                .andExpect(jsonPath("$.weight").value(75.50));
    }
}
