package com.stedfast.fasting.service;

import com.stedfast.fasting.dto.FastingScheduleRequest;
import com.stedfast.fasting.dto.FastingSessionRequest;
import com.stedfast.fasting.models.FastingSchedule;
import com.stedfast.fasting.models.FastingSession;
import com.stedfast.fasting.repository.FastingScheduleRepository;
import com.stedfast.fasting.repository.FastingSessionRepository;
import com.stedfast.user.models.User;
import com.stedfast.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FastingService {

    private final FastingScheduleRepository scheduleRepository;
    private final FastingSessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Transactional
    public FastingSchedule createSchedule(String userId, FastingScheduleRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FastingSchedule schedule = new FastingSchedule();
        schedule.setUser(user);
        schedule.setFastingHours(request.getFastingHours());
        schedule.setEatingHours(request.getEatingHours());
        schedule.setLabel(request.getLabel());

        return scheduleRepository.save(schedule);
    }

    @Transactional
    public FastingSession startSession(String userId, FastingSessionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check for active session
        sessionRepository.findByUserIdAndStatus(userId, FastingSession.SessionStatus.ACTIVE)
                .ifPresent(s -> {
                    throw new RuntimeException("Active session already exists");
                });

        FastingSession session = new FastingSession();
        session.setUser(user);
        session.setSessionType(request.getSessionType());
        session.setStatus(FastingSession.SessionStatus.ACTIVE);

        if (request.getScheduleId() != null) {
            FastingSchedule schedule = scheduleRepository.findById(request.getScheduleId())
                    .orElseThrow(() -> new RuntimeException("Schedule not found"));
            session.setSchedule(schedule);
        }

        return sessionRepository.save(session);
    }

    @Transactional
    public FastingSession endSession(String userId) {
        FastingSession session = sessionRepository.findByUserIdAndStatus(userId, FastingSession.SessionStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("No active session found"));

        ZonedDateTime now = ZonedDateTime.now();
        session.setEndedAt(now);
        session.setStatus(FastingSession.SessionStatus.COMPLETED);

        long minutes = ChronoUnit.MINUTES.between(session.getStartedAt(), now);
        session.setDurationMinutes((int) minutes);

        return sessionRepository.save(session);
    }

    public List<FastingSchedule> getSchedules(String userId) {
        return scheduleRepository.findAllByUserId(userId);
    }

    public FastingSchedule getActiveSchedule(String userId) {
        Optional<FastingSchedule> optFastingSchedule = scheduleRepository.findByUserIdAndIsActiveTrue(userId);
        return optFastingSchedule.orElse(null);
    }

    public Optional<FastingSession> getActiveSession(String userId) {
        return sessionRepository.findByUserIdAndStatus(userId, FastingSession.SessionStatus.ACTIVE);
    }
}
