package com.tanmay.makemytrip_backend.auth.service;

import com.tanmay.makemytrip_backend.auth.dto.AuthResponse;
import com.tanmay.makemytrip_backend.auth.dto.LoginRequest;
import com.tanmay.makemytrip_backend.security.jwt.JwtService;
import com.tanmay.makemytrip_backend.security.service.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        String token = jwtService.generateToken(authentication);

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        return new AuthResponse(
                token,
                "Bearer",
                userDetails.getUser().getId(),
                userDetails.getUser().getEmail(),
                userDetails.getUser().getRole()
        );
    }
}