package com.stedfast.fasting.controller;

import com.stedfast.fasting.dto.CurrentFastingResponse;
import com.stedfast.fasting.dto.FastingChangeRequest;
import com.stedfast.fasting.service.FastingService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fasting")
@RequiredArgsConstructor
public class FastingController {

    private final FastingService fastingService;

    @GetMapping("/{userId}/current-status")
    public ResponseEntity<CurrentFastingResponse> getCurrentFasting(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(fastingService.getCurrentFasting(userId));
    }

    @PostMapping("/{userId}/change-status")
    public ResponseEntity<CurrentFastingResponse> changeStatus(@PathVariable("userId") String userId,
            @RequestBody FastingChangeRequest request) {
        return ResponseEntity.ok(fastingService.updateFastingStatus(userId, request));
    }

}
