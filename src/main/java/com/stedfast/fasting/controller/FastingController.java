package com.stedfast.fasting.controller;

import com.stedfast.fasting.dto.CurrentFastingResponse;
import com.stedfast.fasting.dto.FastingChangeRequest;
import com.stedfast.fasting.service.FastingService;
import com.stedfast.security.SecurityUser;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fasting")
@RequiredArgsConstructor
public class FastingController {

    private final FastingService fastingService;

    @GetMapping("/current-status")
    public ResponseEntity<CurrentFastingResponse> getCurrentFasting(
            @AuthenticationPrincipal SecurityUser user) {
        return ResponseEntity.ok(fastingService.getCurrentFasting(user.getUserId()));
    }

    @PostMapping("/change-status")
    public ResponseEntity<CurrentFastingResponse> changeStatus(
            @AuthenticationPrincipal SecurityUser user,
            @RequestBody FastingChangeRequest request) {
        return ResponseEntity.ok(fastingService.updateFastingStatus(user.getUserId(), request));
    }

}
