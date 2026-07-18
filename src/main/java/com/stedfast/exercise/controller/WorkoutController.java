package com.stedfast.exercise.controller;

import com.stedfast.exercise.dto.ExerciseRequest;
import com.stedfast.exercise.dto.WorkoutLogRequest;
import com.stedfast.exercise.models.Exercise;
import com.stedfast.exercise.models.WorkoutLog;
import com.stedfast.exercise.service.WorkoutService;
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
@RequestMapping("/api/exercise")
@RequiredArgsConstructor
@Tag(name = "Exercise Tracking", description = "Endpoints for managing the exercise library and daily workout logs")
public class WorkoutController {

    private final WorkoutService workoutService;

    // ----- Exercise Library -----
    @GetMapping("/library")
    @Operation(summary = "Get user's saved exercises")
    public ResponseEntity<List<Exercise>> getExercises(@AuthenticationPrincipal SecurityUser user) {
        return ResponseEntity.ok(workoutService.getUserExercises(user.getUserId()));
    }

    @PostMapping("/library")
    @Operation(summary = "Create a new exercise template")
    public ResponseEntity<Exercise> createExercise(
            @AuthenticationPrincipal SecurityUser user,
            @RequestBody ExerciseRequest request) {
        return ResponseEntity.ok(workoutService.createExercise(user.getUserId(), request));
    }

    @PutMapping("/library/{id}")
    @Operation(summary = "Update an exercise template")
    public ResponseEntity<Exercise> updateExercise(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable String id,
            @RequestBody ExerciseRequest request) {
        return ResponseEntity.ok(workoutService.updateExercise(user.getUserId(), id, request));
    }

    @DeleteMapping("/library/{id}")
    @Operation(summary = "Delete an exercise template")
    public ResponseEntity<Void> deleteExercise(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable String id) {
        workoutService.deleteExercise(user.getUserId(), id);
        return ResponseEntity.noContent().build();
    }

    // ----- Workout Logs -----
    @GetMapping("/logs")
    @Operation(summary = "Get workout logs for a specific day")
    public ResponseEntity<List<WorkoutLog>> getWorkoutLogs(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(workoutService.getWorkoutLogsForDay(user.getUserId(), date));
    }

    @PostMapping("/logs")
    @Operation(summary = "Create a new workout log")
    public ResponseEntity<WorkoutLog> createWorkoutLog(
            @AuthenticationPrincipal SecurityUser user,
            @RequestBody WorkoutLogRequest request) {
        return ResponseEntity.ok(workoutService.createWorkoutLog(user.getUserId(), request));
    }

    @PutMapping("/logs/{id}")
    @Operation(summary = "Update a workout log")
    public ResponseEntity<WorkoutLog> updateWorkoutLog(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable String id,
            @RequestBody WorkoutLogRequest request) {
        return ResponseEntity.ok(workoutService.updateWorkoutLog(user.getUserId(), id, request));
    }

    @DeleteMapping("/logs/{id}")
    @Operation(summary = "Delete a workout log")
    public ResponseEntity<Void> deleteWorkoutLog(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable String id) {
        workoutService.deleteWorkoutLog(user.getUserId(), id);
        return ResponseEntity.noContent().build();
    }
}
