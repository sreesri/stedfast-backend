package com.stedfast.fasting.service;

import com.stedfast.exception.BadRequestException;
import com.stedfast.exception.ResourceNotFoundException;
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
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        // Disable Existing Schedule
        Optional<FastingSchedule> existingSchedule = scheduleRepository.findByUserIdAndIsActiveTrue(userId);
        if (existingSchedule.isPresent()) {
            existingSchedule.get().setIsActive(false);
            scheduleRepository.save(existingSchedule.get());
        }

        // Create new Schedule
        FastingSchedule schedule = new FastingSchedule();
        schedule.setUser(user);
        schedule.setFastingHours(request.getFastingHours());
        schedule.setEatingHours(request.getEatingHours());
        schedule.setLabel(request.getLabel());
        schedule.setIsActive(true);

        // Start Fasting Session or Eating Session based on the fasting start time
        startFastingSessionBasedOnSchedule(user, schedule, request.getFastingStartTime());

        return scheduleRepository.save(schedule);
    }

    public void startFastingSessionBasedOnSchedule(User user, FastingSchedule schedule,
            ZonedDateTime fastingStartTime) {

        // Check for active session and complete it if present
        sessionRepository.findByUserIdAndStatus(user.getId(), FastingSession.SessionStatus.ACTIVE)
                .ifPresent(session -> {
                    session.setEndedAt(startTime);
                    session.setStatus(FastingSession.SessionStatus.COMPLETED);
                    long minutes = ChronoUnit.MINUTES.between(session.getStartedAt(), startTime);
                    session.setDurationMinutes((int) minutes);
                    sessionRepository.save(session);
                });

        // create fasting session if fasting start time is in the past
        if (fastingStartTime.isBefore(ZonedDateTime.now())) {
            FastingSession session = new FastingSession();
            session.setUser(user);
            session.setSchedule(schedule);
            session.setSessionType(FastingSession.SessionType.FAST);
            session.setStatus(FastingSession.SessionStatus.ACTIVE);
            session.setStartedAt(fastingStartTime);
            sessionRepository.save(session);
        } // create eating session if the fasting start time is in the future
        else {
            FastingSession session = new FastingSession();
            session.setUser(user);
            session.setSchedule(schedule);
            session.setSessionType(FastingSession.SessionType.EAT);
            session.setStatus(FastingSession.SessionStatus.ACTIVE);
            session.setStartedAt(ZonedDateTime.now());
            sessionRepository.save(session);
        }
    }

    @Transactional
    public FastingSession startSession(String userId, FastingSessionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        ZonedDateTime startTime = request.getStartTime() != null ? request.getStartTime() : ZonedDateTime.now();

        // Check for active session and complete it if present
        sessionRepository.findByUserIdAndStatus(userId, FastingSession.SessionStatus.ACTIVE)
                .ifPresent(session -> {
                    session.setEndedAt(startTime);
                    session.setStatus(FastingSession.SessionStatus.COMPLETED);
                    long minutes = ChronoUnit.MINUTES.between(session.getStartedAt(), startTime);
                    session.setDurationMinutes((int) minutes);
                    sessionRepository.save(session);
                });

        FastingSession session = new FastingSession();
        session.setUser(user);
        session.setSessionType(request.getSessionType());
        session.setStatus(FastingSession.SessionStatus.ACTIVE);
        session.setStartedAt(startTime);

        if (request.getScheduleId() != null) {
            FastingSchedule schedule = scheduleRepository.findById(request.getScheduleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Schedule not found: " + request.getScheduleId()));
            session.setSchedule(schedule);
        }

        return sessionRepository.save(session);
    }

    @Transactional
    public FastingSession endSession(String userId) {
        FastingSession session = sessionRepository.findByUserIdAndStatus(userId, FastingSession.SessionStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("No active session found for user: " + userId));

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
