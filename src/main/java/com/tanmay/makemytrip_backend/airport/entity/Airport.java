package com.tanmay.makemytrip_backend.airport.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "airports")
@EntityListeners(AuditingEntityListener.class)
public class Airport {

    // ==================== ID ====================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==================== AIRPORT INFORMATION ====================

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "code", nullable = false, unique = true, length = 10)
    private String code;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    // ==================== ACCOUNT STATUS ====================

    @Column(name = "active", nullable = false)
    private Boolean active;

    // ==================== AUDITING ====================

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ==================== CONSTRUCTORS ====================

    protected Airport() {
        // Required by JPA
    }

    public Airport(
            String name,
            String code,
            String city,
            String country) {

        this.name = name;
        this.code = code;
        this.city = city;
        this.country = country;
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

    public void setActive(Boolean active) {
        this.active = active;
    }
}