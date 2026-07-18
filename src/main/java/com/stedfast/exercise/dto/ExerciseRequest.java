package com.stedfast.exercise.dto;

import com.stedfast.exercise.models.MuscleGroup;
import lombok.Data;

@Data
public class ExerciseRequest {
    private String name;
    private MuscleGroup muscleGroup;
}
