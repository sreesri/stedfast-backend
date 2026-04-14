package com.stedfast.meal.repository;

import com.stedfast.meal.models.UserIntakeSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserIntakeSummaryRepository extends JpaRepository<UserIntakeSummary, String> {
    Optional<UserIntakeSummary> findByUserIdAndLoggedDateBetween(String userId, ZonedDateTime start, ZonedDateTime end);
    List<UserIntakeSummary> findAllByUserIdAndLoggedDateBetweenOrderByLoggedDateAsc(String userId, ZonedDateTime start, ZonedDateTime end);
}
