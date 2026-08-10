package com.tanmay.makemytrip_backend.user.controller;

import com.tanmay.makemytrip_backend.user.dto.UserRequest;
import com.tanmay.makemytrip_backend.user.dto.UserResponse;
import com.tanmay.makemytrip_backend.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponse createUser(@Valid @RequestBody UserRequest request) {
        return userService.createUser(request);
    }
}