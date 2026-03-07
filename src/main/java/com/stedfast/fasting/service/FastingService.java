package com.stedfast.fasting.service;

import com.stedfast.fasting.dto.FastingStatusResponse;
import com.stedfast.fasting.repository.FastingLogRepository;
import com.stedfast.fasting.models.FastingLog;
import com.stedfast.fasting.models.FastingStatus;

import com.stedfast.user.models.User;
import com.stedfast.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FastingService {

        private final FastingLogRepository fastingLogRepository;
        private final UserRepository userRepository;

        public FastingStatusResponse getFastingStatus(Long userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Optional<FastingLog> activeLog = fastingLogRepository.findFirstByUserAndStatusOrderByStartTimeDesc(user,
                                FastingStatus.FASTING);

                if (activeLog.isPresent()) {
                        FastingLog log = activeLog.get();
                        return FastingStatusResponse.builder()
                                        .id(log.getId())
                                        .userId(log.getUser().getId())
                                        .status(log.getStatus())
                                        .startTime(log.getStartTime())
                                        .endTime(log.getEndTime())
                                        .build();
                }

                return FastingStatusResponse.builder()
                                .userId(userId)
                                .status(FastingStatus.COMPLETED)
                                .build();
        }

}
