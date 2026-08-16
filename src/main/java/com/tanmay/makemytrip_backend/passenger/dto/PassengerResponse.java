package com.tanmay.makemytrip_backend.passenger.dto;

import com.tanmay.makemytrip_backend.passenger.entity.Gender;
import com.tanmay.makemytrip_backend.passenger.entity.PassengerType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PassengerResponse {

    // ==================== PASSENGER INFORMATION ====================

    private final Long id;
    private final Long bookingId;
    private final String firstName;
    private final String lastName;
    private final LocalDate dateOfBirth;
    private final Gender gender;
    private final PassengerType passengerType;

    // ==================== AUDITING ====================

    private final LocalDateTime createdAt;

    // ==================== CONSTRUCTOR ====================

    public PassengerResponse(
            Long id,
            Long bookingId,
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            Gender gender,
            PassengerType passengerType,
            LocalDateTime createdAt) {

        this.id = id;
        this.bookingId = bookingId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.passengerType = passengerType;
        this.createdAt = createdAt;
    }

    // ==================== GETTERS ====================

    public Long getId() {
        return id;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public PassengerType getPassengerType() {
        return passengerType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}