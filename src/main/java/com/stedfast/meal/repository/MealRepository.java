package com.stedfast.meal.repository;

import com.stedfast.meal.models.Meal;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealRepository extends JpaRepository<Meal, String> {

    @EntityGraph(attributePaths = "dishes")
    List<Meal> findAllByUser_IdAndCreatedAtBetweenOrderByCreatedAtDesc(
            String userId,
            ZonedDateTime start,
            ZonedDateTime end
    );

    @EntityGraph(attributePaths = "dishes")
    Optional<Meal> findByIdAndUser_Id(String id, String userId);

    @EntityGraph(attributePaths = "dishes")
    List<Meal> findAllByUser_Id(String userId);
}
