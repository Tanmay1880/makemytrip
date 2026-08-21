package com.tanmay.makemytrip_backend.flight.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FlightRequest {

    // ==================== FLIGHT INFORMATION ====================

    @NotBlank
    @Size(max = 20)
    private String flightNumber;

    @NotNull
    private Long airlineId;

    @NotNull
    private Long departureAirportId;

    @NotNull
    private Long arrivalAirportId;

    // ==================== SCHEDULE ====================

    @NotNull
    @Future
    private LocalDateTime departureTime;

    @NotNull
    @Future
    private LocalDateTime arrivalTime;

    // ==================== SEAT AVAILABILITY ====================

    @NotNull
    @Min(0)
    private Integer economySeatsAvailable;

    @NotNull
    @Min(0)
    private Integer premiumEconomySeatsAvailable;

    @NotNull
    @Min(0)
    private Integer businessSeatsAvailable;

    // ==================== PRICING ====================

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal economyPrice;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal premiumEconomyPrice;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal businessPrice;

    // ==================== GETTERS ====================

    public String getFlightNumber() {
        return flightNumber;
    }

    public Long getAirlineId() {
        return airlineId;
    }

    public Long getDepartureAirportId() {
        return departureAirportId;
    }

    public Long getArrivalAirportId() {
        return arrivalAirportId;
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

    // ==================== SETTERS ====================

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public void setAirlineId(Long airlineId) {
        this.airlineId = airlineId;
    }

    public void setDepartureAirportId(Long departureAirportId) {
        this.departureAirportId = departureAirportId;
    }

    public void setArrivalAirportId(Long arrivalAirportId) {
        this.arrivalAirportId = arrivalAirportId;
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

    public void setPremiumEconomySeatsAvailable(
            Integer premiumEconomySeatsAvailable) {

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
}