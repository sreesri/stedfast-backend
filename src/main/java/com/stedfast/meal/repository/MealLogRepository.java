package com.stedfast.meal.repository;

import com.stedfast.meal.models.MealLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealLogRepository extends JpaRepository<MealLog, String> {
    List<MealLog> findAllByUserIdAndMealTimeBetween(String userId, ZonedDateTime start, ZonedDateTime end);
    Optional<MealLog> findByIdAndUserId(String id, String userId);
}
