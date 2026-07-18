package com.stedfast.user.controller;

import com.stedfast.exception.ForbiddenException;
import com.stedfast.security.SecurityUser;
import com.stedfast.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "Endpoints for managing user accounts")
public class UserController {

    private final UserService userService;

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a user account and all associated data",
            description = "Permanently and irreversibly deletes the account and every piece of data owned by it "
                    + "(body stats, intake limits, fasting schedules/sessions, dishes, meals, meal logs, intake summaries). "
                    + "A user may only delete their own account.")
    public ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable String id) {
        if (!user.getUserId().equals(id)) {
            throw new ForbiddenException("You are not allowed to delete this account");
        }
        userService.deleteUserAccountAndData(id);
        return ResponseEntity.noContent().build();
    }
}
