package com.stedfast.user.controller;

import com.stedfast.security.SecurityUser;
import com.stedfast.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "Endpoints for managing user accounts")
public class UserController {

    private final UserService userService;

    @DeleteMapping("/me")
    @Operation(
            summary = "Delete the current user's account and all associated data",
            description = "Permanently and irreversibly deletes the authenticated user's account and every piece "
                    + "of data owned by it (body stats, intake limits, fasting schedules/sessions, dishes, meals, "
                    + "meal logs, intake summaries). Operates only on the caller's own account, identified from "
                    + "the JWT — there is no way to delete another user's account through this endpoint.")
    public ResponseEntity<Void> deleteCurrentUser(@AuthenticationPrincipal SecurityUser user) {
        userService.deleteUserAccountAndData(user.getUserId());
        return ResponseEntity.noContent().build();
    }
}
