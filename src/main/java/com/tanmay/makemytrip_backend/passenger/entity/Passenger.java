package com.tanmay.makemytrip_backend.passenger.entity;

import com.tanmay.makemytrip_backend.booking.entity.Booking;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "passengers")
@EntityListeners(AuditingEntityListener.class)
public class Passenger {

    // ==================== ID ====================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==================== BOOKING ====================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    // ==================== PASSENGER INFORMATION ====================

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 20)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "passenger_type", nullable = false, length = 20)
    private PassengerType passengerType;

    // ==================== AUDITING ====================

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ==================== CONSTRUCTORS ====================

    protected Passenger() {
        // Required by JPA
    }

    public Passenger(
            Booking booking,
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            Gender gender,
            PassengerType passengerType) {

        this.booking = booking;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.passengerType = passengerType;
    }

    // ==================== GETTERS ====================

    public Long getId() {
        return id;
    }

    public Booking getBooking() {
        return booking;
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

    // ==================== SETTERS ====================

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setPassengerType(PassengerType passengerType) {
        this.passengerType = passengerType;
    }
}