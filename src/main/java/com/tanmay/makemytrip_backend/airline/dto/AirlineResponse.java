package com.tanmay.makemytrip_backend.airline.dto;

import java.time.LocalDateTime;

public class AirlineResponse {

    // ==================== AIRLINE INFORMATION ====================

    private final Long id;
    private final String name;
    private final String code;

    // ==================== ACCOUNT STATUS ====================

    private final Boolean active;

    // ==================== AUDITING ====================

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    // ==================== CONSTRUCTOR ====================

    public AirlineResponse(
            Long id,
            String name,
            String code,
            Boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.name = name;
        this.code = code;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ==================== GETTERS ====================

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public Boolean getActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}