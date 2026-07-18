package com.stedfast.exercise.dto;

import com.stedfast.exercise.models.MuscleGroup;
import lombok.Data;

@Data
public class WorkoutLogExerciseRequest {
    private String exerciseId;
    private String name;
    private MuscleGroup muscleGroup;
    private Integer sets;
    private Integer reps;
}
