package com.stedfast.fasting.service;

import com.stedfast.fasting.dto.FastingChangeRequest;
import com.stedfast.fasting.models.FastingStatus;
import com.stedfast.fasting.repository.UserFastingRepository;
import com.stedfast.fasting.models.UserFasting;
import com.stedfast.fasting.dto.CurrentFastingResponse;
import com.stedfast.user.models.User;
import com.stedfast.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FastingService {

        private final UserRepository userRepository;
        private final UserFastingRepository userFastingRepository;

        public CurrentFastingResponse getCurrentFasting(String userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Optional<UserFasting> userFasting = userFastingRepository.findById(userId);

                if (userFasting.isPresent()) {
                        UserFasting uf = userFasting.get();
                        return CurrentFastingResponse.builder()
                                        .userId(user.getId())
                                        .status(uf.getStatus())
                                        .startTime(uf.getStartTime())
                                        .build();
                } else {
                        return CurrentFastingResponse.builder()
                                        .userId(user.getId())
                                        .status(FastingStatus.IDLE)
                                        .startTime(null)
                                        .build();
                }
        }

        @Transactional
        public CurrentFastingResponse updateFastingStatus(String userId, FastingChangeRequest request) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                UserFasting userFasting = userFastingRepository.findById(userId)
                                .orElseGet(() -> {
                                        UserFasting uf = new UserFasting();
                                        uf.setUserId(userId);
                                        return uf;
                                });

                userFasting.setStatus(request.getStatus());
                userFasting.setStartTime(request.getStartTime());

                userFastingRepository.save(userFasting);

                return CurrentFastingResponse.builder()
                                .userId(user.getId())
                                .status(userFasting.getStatus())
                                .startTime(userFasting.getStartTime())
                                .build();
        }
}
