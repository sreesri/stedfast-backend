package com.stedfast.meal.controller;

import java.time.ZonedDateTime;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stedfast.meal.dto.MealLogRequest;
import com.stedfast.meal.dto.MealLogResponse;
import com.stedfast.meal.service.MealLogService;
import com.stedfast.security.SecurityUser;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/meallog")
@RequiredArgsConstructor
public class MealLogController {

    private final MealLogService mealLogService;

    @GetMapping
    public ResponseEntity<List<MealLogResponse>> getMealLogs(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime date) {
        return ResponseEntity.ok(mealLogService.getMealLogs(user.getUserId(), date));
    }

    @PostMapping
    public ResponseEntity<MealLogResponse> saveMealLog(
            @AuthenticationPrincipal SecurityUser user,
            @RequestBody MealLogRequest request) {
        MealLogResponse mealLog = mealLogService.saveMealLog(user.getUserId(), request);
        return new ResponseEntity<>(mealLog, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMealLog(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable String id) {
        mealLogService.deleteMealLog(user.getUserId(), id);
        return ResponseEntity.noContent().build();
    }
}
