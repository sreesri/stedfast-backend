package com.stedfast.health.repository;

import com.stedfast.health.models.BodyStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BodyStatsRepository extends JpaRepository<BodyStats, String> {
    List<BodyStats> findAllByUserIdOrderByLoggedDateDesc(String userId);
    Optional<BodyStats> findByUserIdAndLoggedDate(String userId, LocalDate loggedDate);
}
