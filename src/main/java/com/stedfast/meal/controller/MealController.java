package com.stedfast.meal.controller;

import com.stedfast.meal.dto.DishRequest;
import com.stedfast.meal.models.Dish;
import com.stedfast.meal.service.MealService;
import com.stedfast.security.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
}
