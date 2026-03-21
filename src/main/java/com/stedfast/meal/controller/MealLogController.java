package com.stedfast.meal.controller;

import java.time.ZonedDateTime;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stedfast.meal.dto.MealLogRequest;
import com.stedfast.meal.dto.MealLogResponse;
import com.stedfast.meal.service.MealLogService;
import com.stedfast.security.SecurityUser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/meallog")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Meal Logs", description = "Endpoints for tracking user meals")
@SecurityRequirement(name = "bearerAuth")
public class MealLogController {

    private final MealLogService mealLogService;

    @GetMapping
    @Operation(summary = "Get meal logs for a specific date", description = "Returns a list of meal logs for the authenticated user and given date")
    public ResponseEntity<List<MealLogResponse>> getMealLogs(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime date) {
        return ResponseEntity.ok(mealLogService.getMealLogs(user.getUserId(), date));
    }

    @PostMapping
    @Operation(summary = "Save a new meal log", description = "Creates a new entry for a meal")
    @ApiResponse(responseCode = "201", description = "Meal log created successfully")
    public ResponseEntity<MealLogResponse> saveMealLog(
            @AuthenticationPrincipal SecurityUser user,
            @RequestBody MealLogRequest request) {
        MealLogResponse mealLog = mealLogService.saveMealLog(user.getUserId(), request);
        return new ResponseEntity<>(mealLog, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a meal log", description = "Removes a meal log entry by ID")
    @ApiResponse(responseCode = "244", description = "Meal log deleted successfully")
    public ResponseEntity<Void> deleteMealLog(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable String id) {
        mealLogService.deleteMealLog(user.getUserId(), id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a meal log", description = "Updates an existing meal log entry by ID")
    public ResponseEntity<MealLogResponse> updateMealLog(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable String id,
            @RequestBody MealLogRequest request) {
        MealLogResponse updatedLog = mealLogService.updateMealLog(user.getUserId(), id, request);
        return ResponseEntity.ok(updatedLog);
    }
}
