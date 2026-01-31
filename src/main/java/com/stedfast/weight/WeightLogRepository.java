package com.stedfast.weight;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeightLogRepository extends JpaRepository<WeightLog, Long> {
}
