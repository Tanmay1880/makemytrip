package com.tanmay.makemytrip_backend.airport.dto;

import java.time.LocalDateTime;

public class AirportResponse {

    // ==================== AIRPORT INFORMATION ====================

    private final Long id;
    private final String name;
    private final String code;
    private final String city;
    private final String country;

    // ==================== ACCOUNT STATUS ====================

    private final Boolean active;

    // ==================== AUDITING ====================

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    // ==================== CONSTRUCTOR ====================

    public AirportResponse(
            Long id,
            String name,
            String code,
            String city,
            String country,
            Boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.name = name;
        this.code = code;
        this.city = city;
        this.country = country;
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

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
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