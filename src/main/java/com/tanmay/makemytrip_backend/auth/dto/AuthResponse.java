package com.tanmay.makemytrip_backend.auth.dto;

import com.tanmay.makemytrip_backend.user.entity.UserRole;

public class AuthResponse {

    private final String accessToken;
    private final String tokenType;
    private final Long userId;
    private final String email;
    private final UserRole role;

    public AuthResponse(
            String accessToken,
            String tokenType,
            Long userId,
            String email,
            UserRole role) {

        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.userId = userId;
        this.email = email;
        this.role = role;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }
}