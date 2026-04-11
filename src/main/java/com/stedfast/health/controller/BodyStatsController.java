package com.stedfast.health.controller;

import com.stedfast.health.dto.BodyStatsRequest;
import com.stedfast.health.models.BodyStats;
import com.stedfast.health.service.BodyStatsService;
import com.stedfast.security.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health/stats")
@RequiredArgsConstructor
@Tag(name = "Body Stats", description = "Endpoints for tracking physical measurements")
public class BodyStatsController {

    private final BodyStatsService bodyStatsService;

    @GetMapping
    @Operation(summary = "Get user body stats", description = "Returns a list of all logged body stats for the authenticated user")
    public ResponseEntity<List<BodyStats>> getStats(@AuthenticationPrincipal SecurityUser user) {
        return ResponseEntity.ok(bodyStatsService.getStatsForUser(user.getUserId()));
    }

    @PostMapping
    @Operation(summary = "Log body stats", description = "Updates or creates body stats for a specific date")
    public ResponseEntity<BodyStats> saveStats(
            @AuthenticationPrincipal SecurityUser user,
            @RequestBody BodyStatsRequest request) {
        return ResponseEntity.ok(bodyStatsService.saveStats(user.getUserId(), request));
    }
}
