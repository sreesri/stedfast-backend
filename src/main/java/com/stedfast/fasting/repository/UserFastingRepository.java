package com.stedfast.fasting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stedfast.fasting.models.UserFasting;

@Repository
public interface UserFastingRepository extends JpaRepository<UserFasting, String> {
}
