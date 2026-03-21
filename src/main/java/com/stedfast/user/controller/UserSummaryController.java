package com.stedfast.user.controller;

import java.time.ZonedDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import com.stedfast.security.SecurityUser;
import com.stedfast.user.dto.UserSummaryResponse;
import com.stedfast.user.service.UserSummaryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user/summary")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Summary", description = "Endpoints for user progress summaries")
@SecurityRequirement(name = "bearerAuth")
public class UserSummaryController {

    private final UserSummaryService userSummaryService;

    @GetMapping
    @Operation(summary = "Get daily summary", description = "Returns a summary of weight, meals, and fasting for a specific date")
    public ResponseEntity<UserSummaryResponse> getSummary(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime date) {
        ZonedDateTime effectiveDate = (date != null) ? date : ZonedDateTime.now();
        return ResponseEntity.ok(userSummaryService.getSummary(user.getUserId(), effectiveDate));
    }
}

