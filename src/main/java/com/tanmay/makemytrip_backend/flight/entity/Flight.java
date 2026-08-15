package com.tanmay.makemytrip_backend.flight.entity;

import com.tanmay.makemytrip_backend.airline.entity.Airline;
import com.tanmay.makemytrip_backend.airport.entity.Airport;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "flights")
@EntityListeners(AuditingEntityListener.class)
public class Flight {

    // ==================== ID ====================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==================== FLIGHT INFORMATION ====================

    @Column(name = "flight_number", nullable = false, length = 20)
    private String flightNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "airline_id", nullable = false)
    private Airline airline;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "departure_airport_id", nullable = false)
    private Airport departureAirport;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "arrival_airport_id", nullable = false)
    private Airport arrivalAirport;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;

    // ==================== SEAT AVAILABILITY ====================

    @Column(name = "economy_seats_available", nullable = false)
    private Integer economySeatsAvailable;

    @Column(name = "premium_economy_seats_available", nullable = false)
    private Integer premiumEconomySeatsAvailable;

    @Column(name = "business_seats_available", nullable = false)
    private Integer businessSeatsAvailable;

    // ==================== PRICING ====================

    @Column(name = "economy_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal economyPrice;

    @Column(name = "premium_economy_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal premiumEconomyPrice;

    @Column(name = "business_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal businessPrice;

    // ==================== STATUS ====================

    @Column(name = "active", nullable = false)
    private Boolean active;

    // ==================== CONCURRENCY ====================

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    // ==================== AUDITING ====================

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ==================== CONSTRUCTORS ====================

    protected Flight() {
        // Required by JPA
    }

    public Flight(
            String flightNumber,
            Airline airline,
            Airport departureAirport,
            Airport arrivalAirport,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime,
            Integer economySeatsAvailable,
            Integer premiumEconomySeatsAvailable,
            Integer businessSeatsAvailable,
            BigDecimal economyPrice,
            BigDecimal premiumEconomyPrice,
            BigDecimal businessPrice) {

        this.flightNumber = flightNumber;
        this.airline = airline;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.economySeatsAvailable = economySeatsAvailable;
        this.premiumEconomySeatsAvailable = premiumEconomySeatsAvailable;
        this.businessSeatsAvailable = businessSeatsAvailable;
        this.economyPrice = economyPrice;
        this.premiumEconomyPrice = premiumEconomyPrice;
        this.businessPrice = businessPrice;
    }

    // ==================== GETTERS ====================

    public Long getId() {
        return id;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public Airline getAirline() {
        return airline;
    }

    public Airport getDepartureAirport() {
        return departureAirport;
    }

    public Airport getArrivalAirport() {
        return arrivalAirport;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public Integer getEconomySeatsAvailable() {
        return economySeatsAvailable;
    }

    public Integer getPremiumEconomySeatsAvailable() {
        return premiumEconomySeatsAvailable;
    }

    public Integer getBusinessSeatsAvailable() {
        return businessSeatsAvailable;
    }

    public BigDecimal getEconomyPrice() {
        return economyPrice;
    }

    public BigDecimal getPremiumEconomyPrice() {
        return premiumEconomyPrice;
    }

    public BigDecimal getBusinessPrice() {
        return businessPrice;
    }

    public Boolean getActive() {
        return active;
    }

    public Long getVersion() {
        return version;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // ==================== SETTERS ====================

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public void setAirline(Airline airline) {
        this.airline = airline;
    }

    public void setDepartureAirport(Airport departureAirport) {
        this.departureAirport = departureAirport;
    }

    public void setArrivalAirport(Airport arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setEconomySeatsAvailable(Integer economySeatsAvailable) {
        this.economySeatsAvailable = economySeatsAvailable;
    }

    public void setPremiumEconomySeatsAvailable(Integer premiumEconomySeatsAvailable) {
        this.premiumEconomySeatsAvailable = premiumEconomySeatsAvailable;
    }

    public void setBusinessSeatsAvailable(Integer businessSeatsAvailable) {
        this.businessSeatsAvailable = businessSeatsAvailable;
    }

    public void setEconomyPrice(BigDecimal economyPrice) {
        this.economyPrice = economyPrice;
    }

    public void setPremiumEconomyPrice(BigDecimal premiumEconomyPrice) {
        this.premiumEconomyPrice = premiumEconomyPrice;
    }

    public void setBusinessPrice(BigDecimal businessPrice) {
        this.businessPrice = businessPrice;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}