package com.stedfast.fasting.controller;

import com.stedfast.fasting.dto.FastingScheduleRequest;
import com.stedfast.fasting.dto.FastingSessionRequest;
import com.stedfast.fasting.models.FastingSchedule;
import com.stedfast.fasting.models.FastingSession;
import com.stedfast.fasting.service.FastingService;
import com.stedfast.security.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fasting")
@RequiredArgsConstructor
@Tag(name = "Fasting", description = "Endpoints for managing fasting schedules and sessions")
public class FastingController {

    private final FastingService fastingService;

    @GetMapping("/schedules")
    @Operation(summary = "Get user schedules")
    public ResponseEntity<List<FastingSchedule>> getSchedules(@AuthenticationPrincipal SecurityUser user) {
        return ResponseEntity.ok(fastingService.getSchedules(user.getUserId()));
    }

    @GetMapping("/schedules/active")
    @Operation(summary = "Get active user schedule")
    public ResponseEntity<FastingSchedule> getActiveSchedule(@AuthenticationPrincipal SecurityUser user) {
        return ResponseEntity.ok(fastingService.getActiveSchedule(user.getUserId()));
    }

    @PostMapping("/schedules")
    @Operation(summary = "Create a new schedule")
    public ResponseEntity<FastingSchedule> createSchedule(
            @AuthenticationPrincipal SecurityUser user,
            @RequestBody FastingScheduleRequest request) {
        return ResponseEntity.ok(fastingService.createSchedule(user.getUserId(), request));
    }

    @GetMapping("/session/active")
    @Operation(summary = "Get active session")
    public ResponseEntity<FastingSession> getActiveSession(@AuthenticationPrincipal SecurityUser user) {
        return fastingService.getActiveSession(user.getUserId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping("/session/start")
    @Operation(summary = "Start a fasting/eating session")
    public ResponseEntity<FastingSession> startSession(
            @AuthenticationPrincipal SecurityUser user,
            @RequestBody FastingSessionRequest request) {
        return ResponseEntity.ok(fastingService.startSession(user.getUserId(), request));
    }

}
