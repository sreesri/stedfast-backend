package com.stedfast.meal.repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stedfast.meal.models.MealLog;

@Repository
public interface MealLogRepository extends JpaRepository<MealLog, String> {

    List<MealLog> findAllByUser_IdAndMealTimeBetween(String userId, ZonedDateTime start, ZonedDateTime end);

    Optional<MealLog> findByIdAndUser_Id(String id, String userId);

}
