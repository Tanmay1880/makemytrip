package com.tanmay.makemytrip_backend.payment.dto;

import jakarta.validation.constraints.NotNull;

public class PaymentRequest {

    @NotNull
    private Long bookingId;

    // ==================== GETTERS ====================

    public Long getBookingId() {
        return bookingId;
    }

    // ==================== SETTERS ====================

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }
}