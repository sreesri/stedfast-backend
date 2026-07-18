package com.stedfast.exercise.repository;

import com.stedfast.exercise.models.WorkoutLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, String> {
    List<WorkoutLog> findAllByUserIdAndLogDateOrderByCreatedAtAsc(String userId, LocalDate logDate);

    Optional<WorkoutLog> findByIdAndUserId(String id, String userId);
}
