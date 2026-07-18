package com.stedfast.exercise.service;

import com.stedfast.exception.ResourceNotFoundException;
import com.stedfast.exercise.dto.ExerciseRequest;
import com.stedfast.exercise.dto.WorkoutLogExerciseRequest;
import com.stedfast.exercise.dto.WorkoutLogRequest;
import com.stedfast.exercise.models.Exercise;
import com.stedfast.exercise.models.WorkoutLog;
import com.stedfast.exercise.models.WorkoutLogExercise;
import com.stedfast.exercise.repository.ExerciseRepository;
import com.stedfast.exercise.repository.WorkoutLogRepository;
import com.stedfast.user.models.User;
import com.stedfast.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final ExerciseRepository exerciseRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final UserRepository userRepository;

    // ----- Exercise Library -----
    @Transactional
    public Exercise createExercise(String userId, ExerciseRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Exercise exercise = new Exercise();
        exercise.setUser(user);
        exercise.setName(request.getName());
        exercise.setMuscleGroup(request.getMuscleGroup());

        return exerciseRepository.save(exercise);
    }

    public List<Exercise> getUserExercises(String userId) {
        return exerciseRepository.findAllByUserId(userId);
    }

    @Transactional
    public Exercise updateExercise(String userId, String exerciseId, ExerciseRequest request) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found: " + exerciseId));

        if (!exercise.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Exercise not found: " + exerciseId);
        }

        exercise.setName(request.getName());
        exercise.setMuscleGroup(request.getMuscleGroup());

        return exerciseRepository.save(exercise);
    }

    @Transactional
    public void deleteExercise(String userId, String exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found: " + exerciseId));

        if (!exercise.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Unauthorized access to exercise: " + exerciseId);
        }

        exerciseRepository.delete(exercise);
    }

    // ----- Workout Logs -----
    @Transactional
    public WorkoutLog createWorkoutLog(String userId, WorkoutLogRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        WorkoutLog workoutLog = new WorkoutLog();
        workoutLog.setUser(user);
        workoutLog.setLogDate(request.getLogDate() != null ? request.getLogDate() : LocalDate.now());
        workoutLog.setNotes(request.getNotes());
        workoutLog.setMuscleGroups(request.getMuscleGroups() != null ? request.getMuscleGroups() : Collections.emptyList());
        workoutLog.setExercises(buildExerciseEntries(userId, workoutLog, request.getExercises()));

        return workoutLogRepository.save(workoutLog);
    }

    public List<WorkoutLog> getWorkoutLogsForDay(String userId, LocalDate date) {
        return workoutLogRepository.findAllByUserIdAndLogDateOrderByCreatedAtAsc(userId, date);
    }

    @Transactional
    public WorkoutLog updateWorkoutLog(String userId, String workoutLogId, WorkoutLogRequest request) {
        WorkoutLog workoutLog = workoutLogRepository.findByIdAndUserId(workoutLogId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout log not found: " + workoutLogId));

        workoutLog.setLogDate(request.getLogDate() != null ? request.getLogDate() : workoutLog.getLogDate());
        workoutLog.setNotes(request.getNotes());
        workoutLog.setMuscleGroups(request.getMuscleGroups() != null ? request.getMuscleGroups() : Collections.emptyList());

        workoutLog.getExercises().clear();
        workoutLog.getExercises().addAll(buildExerciseEntries(userId, workoutLog, request.getExercises()));

        return workoutLogRepository.save(workoutLog);
    }

    @Transactional
    public void deleteWorkoutLog(String userId, String workoutLogId) {
        WorkoutLog workoutLog = workoutLogRepository.findByIdAndUserId(workoutLogId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout log not found: " + workoutLogId));

        workoutLogRepository.delete(workoutLog);
    }

    private List<WorkoutLogExercise> buildExerciseEntries(
            String userId, WorkoutLog workoutLog, List<WorkoutLogExerciseRequest> requests) {
        if (requests == null) {
            return Collections.emptyList();
        }

        return requests.stream().map(eRequest -> {
            WorkoutLogExercise entry = new WorkoutLogExercise();
            entry.setWorkoutLog(workoutLog);
            entry.setSets(eRequest.getSets() != null ? eRequest.getSets() : 1);
            entry.setReps(eRequest.getReps() != null ? eRequest.getReps() : 1);

            if (eRequest.getExerciseId() != null) {
                Exercise template = exerciseRepository.findById(eRequest.getExerciseId())
                        .orElseThrow(() -> new ResourceNotFoundException("Exercise not found: " + eRequest.getExerciseId()));
                if (!template.getUser().getId().equals(userId)) {
                    throw new ResourceNotFoundException("Exercise not found: " + eRequest.getExerciseId());
                }
                entry.setExercise(template);
                entry.setName(template.getName());
                entry.setMuscleGroup(template.getMuscleGroup());
            } else {
                entry.setName(eRequest.getName());
                entry.setMuscleGroup(eRequest.getMuscleGroup());
            }

            return entry;
        }).collect(Collectors.toList());
    }
}
