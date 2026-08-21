package com.tanmay.makemytrip_backend.flight.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FlightResponse {

    // ==================== FLIGHT INFORMATION ====================

    private final Long id;
    private final String flightNumber;

    // ==================== AIRLINE ====================

    private final Long airlineId;
    private final String airlineName;
    private final String airlineCode;

    // ==================== DEPARTURE AIRPORT ====================

    private final Long departureAirportId;
    private final String departureAirportName;
    private final String departureAirportCode;

    // ==================== ARRIVAL AIRPORT ====================

    private final Long arrivalAirportId;
    private final String arrivalAirportName;
    private final String arrivalAirportCode;

    // ==================== SCHEDULE ====================

    private final LocalDateTime departureTime;
    private final LocalDateTime arrivalTime;

    // ==================== SEAT AVAILABILITY ====================

    private final Integer economySeatsAvailable;
    private final Integer premiumEconomySeatsAvailable;
    private final Integer businessSeatsAvailable;

    // ==================== PRICING ====================

    private final BigDecimal economyPrice;
    private final BigDecimal premiumEconomyPrice;
    private final BigDecimal businessPrice;

    // ==================== STATUS ====================

    private final Boolean active;

    // ==================== AUDITING ====================

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    // ==================== CONSTRUCTOR ====================

    public FlightResponse(
            Long id,
            String flightNumber,
            Long airlineId,
            String airlineName,
            String airlineCode,
            Long departureAirportId,
            String departureAirportName,
            String departureAirportCode,
            Long arrivalAirportId,
            String arrivalAirportName,
            String arrivalAirportCode,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime,
            Integer economySeatsAvailable,
            Integer premiumEconomySeatsAvailable,
            Integer businessSeatsAvailable,
            BigDecimal economyPrice,
            BigDecimal premiumEconomyPrice,
            BigDecimal businessPrice,
            Boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.flightNumber = flightNumber;

        this.airlineId = airlineId;
        this.airlineName = airlineName;
        this.airlineCode = airlineCode;

        this.departureAirportId = departureAirportId;
        this.departureAirportName = departureAirportName;
        this.departureAirportCode = departureAirportCode;

        this.arrivalAirportId = arrivalAirportId;
        this.arrivalAirportName = arrivalAirportName;
        this.arrivalAirportCode = arrivalAirportCode;

        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;

        this.economySeatsAvailable = economySeatsAvailable;
        this.premiumEconomySeatsAvailable = premiumEconomySeatsAvailable;
        this.businessSeatsAvailable = businessSeatsAvailable;

        this.economyPrice = economyPrice;
        this.premiumEconomyPrice = premiumEconomyPrice;
        this.businessPrice = businessPrice;

        this.active = active;

        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ==================== GETTERS ====================

    public Long getId() {
        return id;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public Long getAirlineId() {
        return airlineId;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public String getAirlineCode() {
        return airlineCode;
    }

    public Long getDepartureAirportId() {
        return departureAirportId;
    }

    public String getDepartureAirportName() {
        return departureAirportName;
    }

    public String getDepartureAirportCode() {
        return departureAirportCode;
    }

    public Long getArrivalAirportId() {
        return arrivalAirportId;
    }

    public String getArrivalAirportName() {
        return arrivalAirportName;
    }

    public String getArrivalAirportCode() {
        return arrivalAirportCode;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}