package com.stedfast.fasting.controller;

import com.stedfast.fasting.dto.CurrentFastingResponse;
import com.stedfast.fasting.service.FastingService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/fasting")
@RequiredArgsConstructor
public class FastingController {

    private final FastingService fastingService;

    @GetMapping("/{userId}/current-status")
    public ResponseEntity<CurrentFastingResponse> getCurrentFasting(@RequestParam String userIdString) {
        return null;
    }

}
