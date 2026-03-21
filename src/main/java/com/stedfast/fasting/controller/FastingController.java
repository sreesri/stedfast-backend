package com.stedfast.fasting.controller;

import com.stedfast.fasting.dto.CurrentFastingResponse;
import com.stedfast.fasting.dto.FastingChangeRequest;
import com.stedfast.fasting.service.FastingService;
import com.stedfast.security.SecurityUser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/fasting")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Fasting", description = "Endpoints for managing fasting cycles")
@SecurityRequirement(name = "bearerAuth")
public class FastingController {

    private final FastingService fastingService;

    @GetMapping("/current-status")
    @Operation(summary = "Get current fasting status", description = "Returns the current fasting state for the user")
    public ResponseEntity<CurrentFastingResponse> getCurrentFasting(
            @AuthenticationPrincipal SecurityUser user) {
        return ResponseEntity.ok(fastingService.getCurrentFasting(user.getUserId()));
    }

    @PostMapping("/change-status")
    @Operation(summary = "Change fasting status", description = "Starts or breaks a fasting cycle")
    public ResponseEntity<CurrentFastingResponse> changeStatus(
            @AuthenticationPrincipal SecurityUser user,
            @RequestBody FastingChangeRequest request) {
        return ResponseEntity.ok(fastingService.updateFastingStatus(user.getUserId(), request));
    }

}
