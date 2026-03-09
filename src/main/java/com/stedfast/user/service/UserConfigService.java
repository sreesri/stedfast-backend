package com.stedfast.user.service;

import com.stedfast.fasting.dto.FastingChangeRequest;
import com.stedfast.fasting.models.FastingStatus;
import com.stedfast.fasting.service.FastingService;
import com.stedfast.user.models.UserConfig;
import com.stedfast.user.repository.UserConfigRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserConfigService {

    private final UserConfigRepository userConfigRepository;
    private final FastingService fastingService;

    public UserConfig getUserConfig(String userId) {
        return userConfigRepository.findById(userId)
                .orElse(new UserConfig(userId, 18, 6, 2000, java.time.LocalTime.of(19, 0)));
    }

    @Transactional
    public UserConfig saveUserConfig(String userId, UserConfig config) {
        config.setUserId(userId);
        UserConfig userConfig = userConfigRepository.save(config);

        return userConfig;
    }
}
