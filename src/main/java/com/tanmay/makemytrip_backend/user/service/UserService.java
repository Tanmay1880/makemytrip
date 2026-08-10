package com.tanmay.makemytrip_backend.user.service;

import com.tanmay.makemytrip_backend.user.dto.UserRequest;
import com.tanmay.makemytrip_backend.user.dto.UserResponse;
import com.tanmay.makemytrip_backend.user.entity.User;
import com.tanmay.makemytrip_backend.user.entity.UserRole;
import com.tanmay.makemytrip_backend.user.mapper.UserMapper;
import com.tanmay.makemytrip_backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.tanmay.makemytrip_backend.user.exception.UserAlreadyExistsException;


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

    public UserResponse createUser(UserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "User with this email already exists"
            );
        }

        User user = userMapper.toEntity(request);

        // Business rules
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);
        user.setActive(true);
        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }
}