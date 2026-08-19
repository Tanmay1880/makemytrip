package com.tanmay.makemytrip_backend.payment.service;

import com.tanmay.makemytrip_backend.booking.repository.BookingRepository;
import com.tanmay.makemytrip_backend.flight.service.FlightService;
import com.tanmay.makemytrip_backend.passenger.repository.PassengerRepository;
import com.tanmay.makemytrip_backend.payment.entity.Payment;
import com.tanmay.makemytrip_backend.payment.entity.PaymentStatus;
import com.tanmay.makemytrip_backend.payment.mapper.PaymentMapper;
import com.tanmay.makemytrip_backend.payment.repository.PaymentRepository;
import com.tanmay.makemytrip_backend.payment.gateway.PaymentGateway;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.Mockito.*;

class PaymentServiceRefundTest {

    @Test
    void refundPayment_shouldMarkSuccessfulPaymentAsRefunded() {

        // ==================== MOCK DEPENDENCIES ====================

        PaymentRepository paymentRepository =
                mock(PaymentRepository.class);

        PaymentMapper paymentMapper =
                mock(PaymentMapper.class);

        BookingRepository bookingRepository =
                mock(BookingRepository.class);

        PassengerRepository passengerRepository =
                mock(PassengerRepository.class);

        FlightService flightService =
                mock(FlightService.class);

        PaymentGateway paymentGateway =
                mock(PaymentGateway.class);

        // ==================== CREATE SERVICE ====================

        PaymentService paymentService = new PaymentService(
                paymentRepository,
                paymentMapper,
                bookingRepository,
                passengerRepository,
                flightService,
                paymentGateway
        );

        // ==================== TEST PAYMENT ====================

        Payment payment = mock(Payment.class);

        when(payment.getStatus())
                .thenReturn(PaymentStatus.SUCCESS);

        when(paymentRepository.findByBookingIdAndStatus(
                13L,
                PaymentStatus.SUCCESS
        )).thenReturn(Optional.of(payment));

        // ==================== EXECUTE ====================

        paymentService.refundPayment(13L);

        // ==================== VERIFY ====================

        verify(payment)
                .setStatus(PaymentStatus.REFUNDED);

        verify(payment)
                .setProcessedAt(any());

        verify(paymentRepository)
                .save(payment);
    }
}