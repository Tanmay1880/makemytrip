package com.tanmay.makemytrip_backend.booking.dto;

import com.tanmay.makemytrip_backend.booking.entity.BookingStatus;
import com.tanmay.makemytrip_backend.booking.entity.SeatClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BookingResponse {

    // ==================== BOOKING INFORMATION ====================

    private final Long id;
    private final String pnr;

    // ==================== USER ====================

    private final Long userId;

    // ==================== FLIGHT ====================

    private final Long flightId;
    private final String flightNumber;

    // ==================== BOOKING DETAILS ====================

    private final SeatClass seatClass;
    private final BookingStatus status;
    private final BigDecimal totalAmount;

    // ==================== LIFECYCLE ====================

    private final LocalDateTime expiresAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime cancelledAt;

    // ==================== CONSTRUCTOR ====================

    public BookingResponse(
            Long id,
            String pnr,
            Long userId,
            Long flightId,
            String flightNumber,
            SeatClass seatClass,
            BookingStatus status,
            BigDecimal totalAmount,
            LocalDateTime expiresAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime cancelledAt) {

        this.id = id;
        this.pnr = pnr;
        this.userId = userId;
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.seatClass = seatClass;
        this.status = status;
        this.totalAmount = totalAmount;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.cancelledAt = cancelledAt;
    }

    // ==================== GETTERS ====================

    public Long getId() {
        return id;
    }

    public String getPnr() {
        return pnr;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getFlightId() {
        return flightId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public SeatClass getSeatClass() {
        return seatClass;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }
}