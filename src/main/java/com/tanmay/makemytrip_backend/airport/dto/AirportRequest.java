package com.tanmay.makemytrip_backend.airport.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AirportRequest {

    // ==================== AIRPORT INFORMATION ====================

    @NotBlank
    @Size(max = 150)
    private String name;

    @NotBlank
    @Size(max = 10)
    private String code;

    @NotBlank
    @Size(max = 100)
    private String city;

    @NotBlank
    @Size(max = 100)
    private String country;

    // ==================== GETTERS ====================

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

    // ==================== SETTERS ====================

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}