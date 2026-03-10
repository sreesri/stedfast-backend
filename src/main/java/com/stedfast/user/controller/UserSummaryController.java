package com.stedfast.user.controller;

import java.time.ZonedDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stedfast.security.SecurityUser;
import com.stedfast.user.dto.UserSummaryResponse;
import com.stedfast.user.service.UserSummaryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user/summary")
@RequiredArgsConstructor
public class UserSummaryController {

    private final UserSummaryService userSummaryService;

    @GetMapping
    public ResponseEntity<UserSummaryResponse> getSummary(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime date) {
        ZonedDateTime effectiveDate = (date != null) ? date : ZonedDateTime.now();
        return ResponseEntity.ok(userSummaryService.getSummary(user.getUserId(), effectiveDate));
    }
}

