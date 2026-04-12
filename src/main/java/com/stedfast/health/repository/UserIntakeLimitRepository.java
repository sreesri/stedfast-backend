package com.stedfast.health.repository;

import com.stedfast.health.models.UserIntakeLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserIntakeLimitRepository extends JpaRepository<UserIntakeLimit, String> {
    Optional<UserIntakeLimit> findByUserId(String userId);
}
