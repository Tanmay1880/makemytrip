package com.tanmay.makemytrip_backend.auth.controller;

import com.tanmay.makemytrip_backend.auth.dto.AuthResponse;
import com.tanmay.makemytrip_backend.auth.dto.LoginRequest;
import com.tanmay.makemytrip_backend.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest request) {

        return authService.login(request);
    }
}