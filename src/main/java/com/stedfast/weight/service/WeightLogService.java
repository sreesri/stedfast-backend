package com.stedfast.weight.service;

import com.stedfast.weight.dto.WeightLogRequest;
import com.stedfast.weight.repository.WeightLogRepository;
import com.stedfast.weight.models.WeightLog;

import com.stedfast.user.models.User;
import com.stedfast.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WeightLogService {

    private final WeightLogRepository weightLogRepository;
    private final UserRepository userRepository;

    @Transactional
    public WeightLog logWeight(String userId, WeightLogRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        WeightLog weightLog = new WeightLog();
        weightLog.setUser(user);
        weightLog.setWeight(request.getWeight());

        return weightLogRepository.save(weightLog);
    }
}
