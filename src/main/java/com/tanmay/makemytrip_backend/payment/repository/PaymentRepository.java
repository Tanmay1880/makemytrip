package com.tanmay.makemytrip_backend.payment.repository;

import com.tanmay.makemytrip_backend.payment.entity.Payment;
import com.tanmay.makemytrip_backend.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByBookingIdAndStatus(
            Long bookingId,
            PaymentStatus status
    );
}