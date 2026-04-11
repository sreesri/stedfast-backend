package com.stedfast.health.service;

import com.stedfast.health.dto.BodyStatsRequest;
import com.stedfast.health.models.BodyStats;
import com.stedfast.health.repository.BodyStatsRepository;
import com.stedfast.user.models.User;
import com.stedfast.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BodyStatsService {

    private final BodyStatsRepository bodyStatsRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<BodyStats> getStatsForUser(String userId) {
        return bodyStatsRepository.findAllByUserIdOrderByLoggedDateDesc(userId);
    }

    @Transactional
    public BodyStats saveStats(String userId, BodyStatsRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BodyStats stats = bodyStatsRepository.findByUserIdAndLoggedDate(userId, request.getLoggedDate())
                .orElse(new BodyStats());

        stats.setUser(user);
        stats.setLoggedDate(request.getLoggedDate());
        stats.setHeightCm(request.getHeightCm());
        stats.setWeightKg(request.getWeightKg());
        stats.setWaistCm(request.getWaistCm());
        stats.setChestCm(request.getChestCm());
        stats.setHipsCm(request.getHipsCm());
        stats.setBodyFatPct(request.getBodyFatPct());

        return bodyStatsRepository.save(stats);
    }
}
