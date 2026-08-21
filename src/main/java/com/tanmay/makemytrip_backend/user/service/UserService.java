package com.tanmay.makemytrip_backend.user.service;

import com.tanmay.makemytrip_backend.user.dto.UserRequest;
import com.tanmay.makemytrip_backend.user.dto.UserResponse;
import com.tanmay.makemytrip_backend.user.dto.UserUpdateRequest;
import com.tanmay.makemytrip_backend.user.entity.User;
import com.tanmay.makemytrip_backend.user.entity.UserRole;
import com.tanmay.makemytrip_backend.user.exception.UserAlreadyExistsException;
import com.tanmay.makemytrip_backend.user.exception.UserNotFoundException;
import com.tanmay.makemytrip_backend.user.mapper.UserMapper;
import com.tanmay.makemytrip_backend.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    // ==================== CREATE ====================

    public UserResponse createUser(UserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "User with this email already exists"
            );
        }

        User user = userMapper.toEntity(request);

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        // Controlled by backend.
        user.setRole(UserRole.USER);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    // ==================== READ ====================

    public UserResponse getUserById(Long id) {

        User user = findUser(id);

        validateUserAccess(user);

        return userMapper.toResponse(user);
    }

    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    // ==================== UPDATE ====================

    public UserResponse updateUser(
            Long id,
            UserUpdateRequest request) {

        User user = findUser(id);

        validateUserAccess(user);

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());

        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    // ==================== DELETE ====================

    public void deleteUser(Long id) {

        User user = findUser(id);

        /*
         * DELETE is ADMIN-only in SecurityConfig.
         * Therefore no USER ownership check is required here.
         */

        user.setActive(false);

        userRepository.save(user);
    }

    // ==================== COMMON ====================

    private User findUser(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: " + id
                        )
                );
    }

    private void validateUserAccess(User user) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                );

        if (isAdmin) {
            return;
        }

        if (!user.getEmail().equals(authentication.getName())) {
            throw new UserNotFoundException(
                    "User not found with id: " + user.getId()
            );
        }
    }
}