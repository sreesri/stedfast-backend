package com.stedfast.user.controller;

import com.stedfast.security.SecurityUser;
import com.stedfast.user.models.UserConfig;
import com.stedfast.user.service.UserConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/user/settings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Settings", description = "Endpoints for managing user-specific configurations")
@SecurityRequirement(name = "bearerAuth")
public class UserConfigController {

    private final UserConfigService userConfigService;

    @GetMapping
    @Operation(summary = "Get user settings", description = "Returns the configuration settings for the authenticated user")
    public ResponseEntity<UserConfig> getUserConfig(@AuthenticationPrincipal SecurityUser user) {
        return ResponseEntity.ok(userConfigService.getUserConfig(user.getUserId()));
    }

    @PostMapping
    @Operation(summary = "Save user settings", description = "Updates the configuration settings for the authenticated user")
    public ResponseEntity<UserConfig> saveUserConfig(
            @AuthenticationPrincipal SecurityUser user,
            @RequestBody UserConfig config) {
        return ResponseEntity.ok(userConfigService.saveUserConfig(user.getUserId(), config));
    }
}
