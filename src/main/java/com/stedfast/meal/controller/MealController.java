package com.stedfast.meal.controller;

import com.stedfast.meal.dto.DishRequest;
import com.stedfast.meal.models.Dish;
import com.stedfast.meal.models.Meal;
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
@Tag(name = "Meal Management", description = "Endpoints for managing dishes and meals")
public class MealController {

    private final MealService mealService;

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

    @GetMapping({"/meals", "/logs"})
    @Operation(summary = "Get meals for a specific day")
    public ResponseEntity<List<Meal>> getMeals(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(mealService.getMealsForDay(user.getUserId(), date));
    }

    @GetMapping("/meals/{id}")
    @Operation(summary = "Get a meal by id")
    public ResponseEntity<Meal> getMeal(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable String id) {
        return ResponseEntity.ok(mealService.getMeal(user.getUserId(), id));
    }

    @PostMapping({"/meals", "/logs"})
    @Operation(summary = "Create a new meal")
    public ResponseEntity<Meal> createMeal(
            @AuthenticationPrincipal SecurityUser user,
            @RequestBody com.stedfast.meal.dto.MealLogRecordRequest request) {
        return ResponseEntity.ok(mealService.createMeal(user.getUserId(), request));
    }

    @DeleteMapping({"/meals/{id}", "/logs/{id}"})
    @Operation(summary = "Delete a meal")
    public ResponseEntity<Void> deleteMeal(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable String id) {
        mealService.deleteMeal(user.getUserId(), id);
        return ResponseEntity.noContent().build();
    }

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
