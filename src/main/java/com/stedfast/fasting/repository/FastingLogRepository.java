package com.stedfast.fasting.repository;

import com.stedfast.fasting.models.FastingLog;
import com.stedfast.fasting.models.FastingStatus;

import com.stedfast.user.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FastingLogRepository extends JpaRepository<FastingLog, String> {

    Optional<FastingLog> findFirstByUserAndStatusOrderByStartTimeDesc(User user, FastingStatus status);

}
