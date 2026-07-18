package com.stedfast.exercise.dto;

import com.stedfast.exercise.models.MuscleGroup;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class WorkoutLogRequest {
    private LocalDate logDate;
    private List<MuscleGroup> muscleGroups;
    private String notes;
    private List<WorkoutLogExerciseRequest> exercises;
}
