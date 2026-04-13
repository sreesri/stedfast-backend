package com.stedfast.fasting.repository;

import com.stedfast.fasting.models.FastingSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FastingScheduleRepository extends JpaRepository<FastingSchedule, String> {
    List<FastingSchedule> findAllByUserId(String userId);

    Optional<FastingSchedule> findByUserIdAndIsActiveTrue(String userId);
}
