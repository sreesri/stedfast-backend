package com.stedfast.weight.controller;

import com.stedfast.security.SecurityUser;
import com.stedfast.weight.dto.WeightLogRequest;
import com.stedfast.weight.service.WeightLogService;
import com.stedfast.weight.models.WeightLog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/weights")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Weight Logs", description = "Endpoints for tracking user weight")
@SecurityRequirement(name = "bearerAuth")
public class WeightLogController {

    private final WeightLogService weightLogService;

    @PostMapping
    @Operation(summary = "Log weight", description = "Creates a new weight entry for the user")
    public ResponseEntity<WeightLog> logWeight(
            @AuthenticationPrincipal SecurityUser user,
            @RequestBody WeightLogRequest request) {
        WeightLog weightLog = weightLogService.logWeight(user.getUserId(), request);
        return new ResponseEntity<>(weightLog, HttpStatus.CREATED);
    }
}
