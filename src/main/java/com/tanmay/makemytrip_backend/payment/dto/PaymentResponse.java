package com.tanmay.makemytrip_backend.payment.dto;

import com.tanmay.makemytrip_backend.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponse {

    // ==================== PAYMENT INFORMATION ====================

    private final Long id;
    private final Long bookingId;
    private final BigDecimal amount;
    private final PaymentStatus status;
    private final String paymentReference;

    // ==================== TIMESTAMPS ====================

    private final LocalDateTime createdAt;
    private final LocalDateTime processedAt;

    // ==================== CONSTRUCTOR ====================

    public PaymentResponse(
            Long id,
            Long bookingId,
            BigDecimal amount,
            PaymentStatus status,
            String paymentReference,
            LocalDateTime createdAt,
            LocalDateTime processedAt) {

        this.id = id;
        this.bookingId = bookingId;
        this.amount = amount;
        this.status = status;
        this.paymentReference = paymentReference;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
    }

    // ==================== GETTERS ====================

    public Long getId() {
        return id;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
}