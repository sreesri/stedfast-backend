package com.stedfast.user.controller;

import com.stedfast.security.SecurityUser;
import com.stedfast.user.models.UserConfig;
import com.stedfast.user.service.UserConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/settings")
@RequiredArgsConstructor
public class UserConfigController {

    private final UserConfigService userConfigService;

    @GetMapping
    public ResponseEntity<UserConfig> getUserConfig(@AuthenticationPrincipal SecurityUser user) {
        return ResponseEntity.ok(userConfigService.getUserConfig(user.getUserId()));
    }

    @PostMapping
    public ResponseEntity<UserConfig> saveUserConfig(
            @AuthenticationPrincipal SecurityUser user,
            @RequestBody UserConfig config) {
        return ResponseEntity.ok(userConfigService.saveUserConfig(user.getUserId(), config));
    }
}
