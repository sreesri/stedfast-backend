package com.stedfast.user.service;

import com.stedfast.exception.ConflictException;
import com.stedfast.exception.ResourceNotFoundException;
import com.stedfast.user.dto.UserCreateRequest;
import com.stedfast.user.models.User;
import com.stedfast.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists: " + request.getEmail());
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    /**
     * Permanently deletes a user account and all associated data (data safety / right-to-erasure compliance).
     * <p>
     * Every user-owned table (fasting_schedules, fasting_sessions, body_stats, dishes, meals, meal_dishes,
     * meal_logs, meal_log_dishes, user_intake_limits, user_intake_summary) declares its user_id foreign key
     * with ON DELETE CASCADE at the database level, so deleting the user row is sufficient to erase all of
     * their data. This keeps deletion correct automatically as new user-owned tables are added, instead of
     * relying on an application-level list that can drift out of sync.
     */
    @Transactional
    public void deleteUserAccountAndData(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        userRepository.delete(user);
    }
}
