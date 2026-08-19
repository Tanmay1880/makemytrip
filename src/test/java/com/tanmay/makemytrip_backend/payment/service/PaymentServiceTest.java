package com.tanmay.makemytrip_backend.payment.service;

import com.tanmay.makemytrip_backend.booking.entity.Booking;
import com.tanmay.makemytrip_backend.booking.entity.BookingStatus;
import com.tanmay.makemytrip_backend.booking.entity.SeatClass;
import com.tanmay.makemytrip_backend.booking.repository.BookingRepository;
import com.tanmay.makemytrip_backend.flight.entity.Flight;
import com.tanmay.makemytrip_backend.flight.service.FlightService;
import com.tanmay.makemytrip_backend.passenger.repository.PassengerRepository;
import com.tanmay.makemytrip_backend.payment.dto.PaymentResponse;
import com.tanmay.makemytrip_backend.payment.entity.Payment;
import com.tanmay.makemytrip_backend.payment.entity.PaymentStatus;
import com.tanmay.makemytrip_backend.payment.gateway.PaymentGateway;
import com.tanmay.makemytrip_backend.payment.gateway.PaymentResult;
import com.tanmay.makemytrip_backend.payment.mapper.PaymentMapper;
import com.tanmay.makemytrip_backend.payment.repository.PaymentRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @Test
    void processPayment_shouldMarkPaymentFailed_whenGatewayReturnsFailed() {

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

        // ==================== TEST DATA ====================

        Booking booking = mock(Booking.class);

        when(booking.getStatus())
                .thenReturn(BookingStatus.PENDING);

        when(booking.getExpiresAt())
                .thenReturn(LocalDateTime.now().plusMinutes(10));

        Payment payment = mock(Payment.class);

        when(payment.getStatus())
                .thenReturn(PaymentStatus.INITIATED);

        when(payment.getBooking())
                .thenReturn(booking);

        when(payment.getAmount())
                .thenReturn(new BigDecimal("500.00"));

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        when(passengerRepository.countByBookingId(anyLong()))
                .thenReturn(1L);

        // ==================== GATEWAY FAILURE ====================

        when(paymentGateway.processPayment(new BigDecimal("500.00")))
                .thenReturn(PaymentResult.FAILED);

        PaymentResponse expectedResponse = mock(PaymentResponse.class);

        when(paymentRepository.save(payment))
                .thenReturn(payment);

        when(paymentMapper.toResponse(payment))
                .thenReturn(expectedResponse);

        // ==================== EXECUTE ====================

        PaymentResponse result =
                paymentService.processPayment(1L);

        // ==================== VERIFY ====================

        verify(payment)
                .setStatus(PaymentStatus.FAILED);

        verify(payment)
                .setProcessedAt(any(LocalDateTime.class));

        verify(paymentRepository)
                .save(payment);

        verify(paymentMapper)
                .toResponse(payment);

        // Failed payment must NOT reserve seats.
        verify(flightService, never())
                .reserveSeats(
                        anyLong(),
                        any(SeatClass.class),
                        anyInt()
                );

        // Booking must NOT be confirmed.
        verify(booking, never())
                .setStatus(BookingStatus.CONFIRMED);

        assertEquals(expectedResponse, result);
    }
}