package com.stedfast.weight;

import com.stedfast.user.User;
import com.stedfast.user.UserRepository;
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
    public WeightLog logWeight(WeightLogRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        WeightLog weightLog = new WeightLog();
        weightLog.setUser(user);
        weightLog.setWeight(request.getWeight());

        return weightLogRepository.save(weightLog);
    }
}
