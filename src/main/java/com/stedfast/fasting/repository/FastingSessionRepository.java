package com.stedfast.fasting.repository;

import com.stedfast.fasting.models.FastingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FastingSessionRepository extends JpaRepository<FastingSession, String> {
    Optional<FastingSession> findByUserIdAndStatus(String userId, FastingSession.SessionStatus status);
}
