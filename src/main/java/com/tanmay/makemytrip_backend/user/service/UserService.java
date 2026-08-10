package com.tanmay.makemytrip_backend.user.service;

import com.tanmay.makemytrip_backend.user.dto.UserRequest;
import com.tanmay.makemytrip_backend.user.dto.UserResponse;
import com.tanmay.makemytrip_backend.user.entity.User;
import com.tanmay.makemytrip_backend.user.entity.UserRole;
import com.tanmay.makemytrip_backend.user.mapper.UserMapper;
import com.tanmay.makemytrip_backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper) {

        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponse createUser(UserRequest request) {

        User user = userMapper.toEntity(request);

        // Business rules
        user.setRole(UserRole.USER);
        user.setActive(true);

        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }
}