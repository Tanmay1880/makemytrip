package com.tanmay.makemytrip_backend.airline.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AirlineRequest {

    // ==================== AIRLINE INFORMATION ====================

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 10)
    private String code;

    // ==================== GETTERS ====================

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    // ==================== SETTERS ====================

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }
}