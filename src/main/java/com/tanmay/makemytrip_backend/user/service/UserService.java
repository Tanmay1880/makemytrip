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

    /**
     * Creates a new user account.
     *
     * The role and active status are controlled by the backend
     * and are not provided by the client.
     */
    public UserResponse createUser(UserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "User with this email already exists"
            );
        }

        User user = userMapper.toEntity(request);

        // Business rules controlled by the backend.
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    // ==================== READ ====================

    /**
     * Retrieves a user by ID.
     *
     * @throws UserNotFoundException if the user does not exist
     */
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: " + id
                        )
                );

        return userMapper.toResponse(user);
    }

    /**
     * Retrieves all users.
     */
    public List<UserResponse> getAllUsers() {

        List<User> users = userRepository.findAll();

        return users.stream()
                .map(userMapper::toResponse)
                .toList();
    }

    // ==================== UPDATE ====================

    /**
     * Updates the editable profile fields of a user.
     *
     * Email, password, role, active status, and audit fields
     * are intentionally not modified here.
     *
     * @throws UserNotFoundException if the user does not exist
     */
    public UserResponse updateUser(
            Long id,
            UserUpdateRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: " + id
                        )
                );

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());

        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    // ==================== DELETE ====================

    /**
     * Deactivates a user instead of physically deleting
     * the database record.
     *
     * This preserves the user's historical data for
     * future relationships such as bookings.
     *
     * @throws UserNotFoundException if the user does not exist
     */
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: " + id
                        )
                );

        user.setActive(false);

        userRepository.save(user);
    }
}