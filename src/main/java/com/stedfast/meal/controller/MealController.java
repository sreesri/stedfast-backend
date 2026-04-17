package com.stedfast.meal.controller;

import com.stedfast.meal.dto.DishRequest;
import com.stedfast.meal.dto.MealLogRecordRequest;
import com.stedfast.meal.dto.MealRequest;
import com.stedfast.meal.models.Dish;
import com.stedfast.meal.models.Meal;
import com.stedfast.meal.models.MealLog;
import com.stedfast.meal.models.UserIntakeSummary;
import com.stedfast.meal.service.MealService;
import com.stedfast.security.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/meal")
@RequiredArgsConstructor
@Tag(name = "Meal Management", description = "Endpoints for managing dishes, meal templates, and daily logs")
public class MealController {

    private final MealService mealService;

    // ----- Dishes -----
    @GetMapping("/dishes")
    @Operation(summary = "Get user dishes")
    public ResponseEntity<List<Dish>> getDishes(@AuthenticationPrincipal SecurityUser user) {
        return ResponseEntity.ok(mealService.getUserDishes(user.getUserId()));
    }

    @PostMapping("/dishes")
    @Operation(summary = "Create a new dish template")
    public ResponseEntity<Dish> createDish(
            @AuthenticationPrincipal SecurityUser user,
            @RequestBody DishRequest request) {
        return ResponseEntity.ok(mealService.createDish(user.getUserId(), request));
    }

    @DeleteMapping("/dishes/{id}")
    @Operation(summary = "Delete a dish template")
    public ResponseEntity<Void> deleteDish(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable String id) {
        mealService.deleteDish(user.getUserId(), id);
        return ResponseEntity.noContent().build();
    }

    // ----- Meal Templates -----
    @GetMapping("/meals")
    @Operation(summary = "Get all meal templates for the user")
    public ResponseEntity<List<Meal>> getMealTemplates(
            @AuthenticationPrincipal SecurityUser user) {
        return ResponseEntity.ok(mealService.getUserMealTemplates(user.getUserId()));
    }

    @GetMapping("/meals/{id}")
    @Operation(summary = "Get a meal template by id")
    public ResponseEntity<Meal> getMealTemplate(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable String id) {
        return ResponseEntity.ok(mealService.getMealTemplate(user.getUserId(), id));
    }

    @PostMapping("/meals")
    @Operation(summary = "Create a new meal template")
    public ResponseEntity<Meal> createMealTemplate(
            @AuthenticationPrincipal SecurityUser user,
            @RequestBody MealRequest request) {
        return ResponseEntity.ok(mealService.createMealTemplate(user.getUserId(), request));
    }

    @DeleteMapping("/meals/{id}")
    @Operation(summary = "Delete a meal template")
    public ResponseEntity<Void> deleteMealTemplate(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable String id) {
        mealService.deleteMealTemplate(user.getUserId(), id);
        return ResponseEntity.noContent().build();
    }

    // ----- Meal Logs -----
    @GetMapping("/logs")
    @Operation(summary = "Get meal logs for a specific day")
    public ResponseEntity<List<MealLog>> getMealLogs(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(mealService.getMealLogsForDay(user.getUserId(), date));
    }

    @GetMapping("/logs/{id}")
    @Operation(summary = "Get a specific meal log by id")
    public ResponseEntity<MealLog> getMealLog(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable String id) {
        return ResponseEntity.ok(mealService.getMealLog(user.getUserId(), id));
    }

    @PostMapping("/logs")
    @Operation(summary = "Create a new meal log")
    public ResponseEntity<MealLog> createMealLog(
            @AuthenticationPrincipal SecurityUser user,
            @RequestBody MealLogRecordRequest request) {
        return ResponseEntity.ok(mealService.createMealLog(user.getUserId(), request));
    }

    @DeleteMapping("/logs/{id}")
    @Operation(summary = "Delete a meal log")
    public ResponseEntity<Void> deleteMealLog(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable String id) {
        mealService.deleteMealLog(user.getUserId(), id);
        return ResponseEntity.noContent().build();
    }

    // ----- Intake Summary -----
    @GetMapping("/intake-summary")
    @Operation(summary = "Get intake summary for a date or range")
    public ResponseEntity<List<UserIntakeSummary>> getIntakeSummary(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        LocalDate start = startDate != null ? startDate : (date != null ? date : LocalDate.now());
        LocalDate end = endDate != null ? endDate : start;

        return ResponseEntity.ok(mealService.getIntakeSummaries(user.getUserId(), start, end));
    }

}
