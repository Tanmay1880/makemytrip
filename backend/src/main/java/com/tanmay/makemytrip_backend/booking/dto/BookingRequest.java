package com.tanmay.makemytrip_backend.booking.dto;

import com.tanmay.makemytrip_backend.booking.entity.SeatClass;
import jakarta.validation.constraints.NotNull;

public class BookingRequest {

    // ==================== BOOKING REFERENCES ====================

    @NotNull
    private Long flightId;

    // ==================== SEAT CLASS ====================

    @NotNull
    private SeatClass seatClass;

    // ==================== GETTERS ====================

    public Long getFlightId() {
        return flightId;
    }

    public SeatClass getSeatClass() {
        return seatClass;
    }

    // ==================== SETTERS ====================

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public void setSeatClass(SeatClass seatClass) {
        this.seatClass = seatClass;
    }
}