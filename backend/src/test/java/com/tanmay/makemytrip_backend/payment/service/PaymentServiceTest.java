package com.tanmay.makemytrip_backend.payment.service;

import com.tanmay.makemytrip_backend.booking.entity.Booking;
import com.tanmay.makemytrip_backend.booking.entity.BookingStatus;
import com.tanmay.makemytrip_backend.booking.entity.SeatClass;
import com.tanmay.makemytrip_backend.booking.exception.BookingNotFoundException;
import com.tanmay.makemytrip_backend.booking.repository.BookingRepository;
import com.tanmay.makemytrip_backend.flight.entity.Flight;
import com.tanmay.makemytrip_backend.flight.service.FlightService;
import com.tanmay.makemytrip_backend.passenger.repository.PassengerRepository;
import com.tanmay.makemytrip_backend.payment.dto.PaymentRequest;
import com.tanmay.makemytrip_backend.payment.dto.PaymentResponse;
import com.tanmay.makemytrip_backend.payment.entity.Payment;
import com.tanmay.makemytrip_backend.payment.entity.PaymentStatus;
import com.tanmay.makemytrip_backend.payment.exception.InvalidPaymentException;
import com.tanmay.makemytrip_backend.payment.gateway.PaymentGateway;
import com.tanmay.makemytrip_backend.payment.gateway.PaymentResult;
import com.tanmay.makemytrip_backend.payment.mapper.PaymentMapper;
import com.tanmay.makemytrip_backend.payment.repository.PaymentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    private PaymentRepository paymentRepository;
    private PaymentMapper paymentMapper;
    private BookingRepository bookingRepository;
    private PassengerRepository passengerRepository;
    private FlightService flightService;
    private PaymentGateway paymentGateway;

    private PaymentService paymentService;

    // ==================== SETUP ====================

    @BeforeEach
    void setUp() {

        paymentRepository = mock(PaymentRepository.class);
        paymentMapper = mock(PaymentMapper.class);
        bookingRepository = mock(BookingRepository.class);
        passengerRepository = mock(PassengerRepository.class);
        flightService = mock(FlightService.class);
        paymentGateway = mock(PaymentGateway.class);

        paymentService = new PaymentService(
                paymentRepository,
                paymentMapper,
                bookingRepository,
                passengerRepository,
                flightService,
                paymentGateway
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "admin@test.com",
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        )
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ==================== CREATE PAYMENT ====================

    @Test
    void createPayment_shouldCreateInitiatedPayment() {

        PaymentRequest request = new PaymentRequest();
        request.setBookingId(10L);

        Booking booking = mock(Booking.class);
        Flight flight = mock(Flight.class);
        Payment payment = mock(Payment.class);
        PaymentResponse expectedResponse = mock(PaymentResponse.class);

        when(bookingRepository.findById(10L))
                .thenReturn(Optional.of(booking));

        when(booking.getStatus())
                .thenReturn(BookingStatus.PENDING);

        when(booking.getExpiresAt())
                .thenReturn(LocalDateTime.now().plusMinutes(10));

        when(booking.getId())
                .thenReturn(10L);

        when(booking.getFlight())
                .thenReturn(flight);

        when(booking.getSeatClass())
                .thenReturn(SeatClass.BUSINESS);

        when(flight.getPrice(SeatClass.BUSINESS))
                .thenReturn(new BigDecimal("1500.00"));

        when(passengerRepository.countByBookingId(10L))
                .thenReturn(2L);

        when(paymentRepository.findByBookingIdAndStatus(
                10L,
                PaymentStatus.SUCCESS
        )).thenReturn(Optional.empty());

        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(payment);

        when(paymentMapper.toResponse(payment))
                .thenReturn(expectedResponse);

        PaymentResponse result =
                paymentService.createPayment(request);

        verify(paymentRepository)
                .save(any(Payment.class));

        verify(paymentMapper)
                .toResponse(payment);

        assertSame(expectedResponse, result);
    }

    @Test
    void createPayment_shouldRejectWhenBookingDoesNotExist() {

        PaymentRequest request = new PaymentRequest();
        request.setBookingId(99L);

        when(bookingRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                BookingNotFoundException.class,
                () -> paymentService.createPayment(request)
        );

        verifyNoInteractions(
                passengerRepository,
                paymentMapper
        );

        verify(paymentRepository, never())
                .save(any());
    }

    @Test
    void createPayment_shouldRejectNonPendingBooking() {

        PaymentRequest request = new PaymentRequest();
        request.setBookingId(10L);

        Booking booking = mock(Booking.class);

        when(bookingRepository.findById(10L))
                .thenReturn(Optional.of(booking));

        when(booking.getStatus())
                .thenReturn(BookingStatus.CONFIRMED);

        assertThrows(
                InvalidPaymentException.class,
                () -> paymentService.createPayment(request)
        );

        verifyNoInteractions(passengerRepository);

        verify(paymentRepository, never())
                .save(any());
    }

    @Test
    void createPayment_shouldRejectExpiredBooking() {

        PaymentRequest request = new PaymentRequest();
        request.setBookingId(10L);

        Booking booking = mock(Booking.class);

        when(bookingRepository.findById(10L))
                .thenReturn(Optional.of(booking));

        when(booking.getStatus())
                .thenReturn(BookingStatus.PENDING);

        when(booking.getExpiresAt())
                .thenReturn(LocalDateTime.now().minusMinutes(1));

        assertThrows(
                InvalidPaymentException.class,
                () -> paymentService.createPayment(request)
        );

        verifyNoInteractions(passengerRepository);

        verify(paymentRepository, never())
                .save(any());
    }

    @Test
    void createPayment_shouldRejectWhenBookingHasNoPassengers() {

        PaymentRequest request = new PaymentRequest();
        request.setBookingId(10L);

        Booking booking = mock(Booking.class);

        when(bookingRepository.findById(10L))
                .thenReturn(Optional.of(booking));

        when(booking.getStatus())
                .thenReturn(BookingStatus.PENDING);

        when(booking.getExpiresAt())
                .thenReturn(LocalDateTime.now().plusMinutes(10));

        when(booking.getId())
                .thenReturn(10L);

        when(passengerRepository.countByBookingId(10L))
                .thenReturn(0L);

        assertThrows(
                InvalidPaymentException.class,
                () -> paymentService.createPayment(request)
        );

        verify(paymentRepository, never())
                .save(any());
    }

    @Test
    void createPayment_shouldRejectWhenSuccessfulPaymentAlreadyExists() {

        PaymentRequest request = new PaymentRequest();
        request.setBookingId(10L);

        Booking booking = mock(Booking.class);
        Flight flight = mock(Flight.class);
        Payment existingPayment = mock(Payment.class);

        when(bookingRepository.findById(10L))
                .thenReturn(Optional.of(booking));

        when(booking.getStatus())
                .thenReturn(BookingStatus.PENDING);

        when(booking.getExpiresAt())
                .thenReturn(LocalDateTime.now().plusMinutes(10));

        when(booking.getId())
                .thenReturn(10L);

        when(booking.getFlight())
                .thenReturn(flight);

        when(booking.getSeatClass())
                .thenReturn(SeatClass.ECONOMY);

        when(flight.getPrice(SeatClass.ECONOMY))
                .thenReturn(new BigDecimal("500.00"));

        when(passengerRepository.countByBookingId(10L))
                .thenReturn(1L);

        when(paymentRepository.findByBookingIdAndStatus(
                10L,
                PaymentStatus.SUCCESS
        )).thenReturn(Optional.of(existingPayment));

        InvalidPaymentException exception =
                assertThrows(
                        InvalidPaymentException.class,
                        () -> paymentService.createPayment(request)
                );

        assertEquals(
                "Booking already has a successful payment",
                exception.getMessage()
        );

        verify(paymentRepository, never())
                .save(any());
    }

    // ==================== PROCESS PAYMENT ====================

    @Test
    void processPayment_shouldConfirmBookingAndReserveSeats_whenPaymentSucceeds() {

        Payment payment = mock(Payment.class);
        Booking booking = mock(Booking.class);
        Flight flight = mock(Flight.class);
        PaymentResponse expectedResponse = mock(PaymentResponse.class);

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        when(payment.getStatus())
                .thenReturn(PaymentStatus.INITIATED);

        when(payment.getBooking())
                .thenReturn(booking);

        when(payment.getAmount())
                .thenReturn(new BigDecimal("1500.00"));

        when(booking.getStatus())
                .thenReturn(BookingStatus.PENDING);

        when(booking.getExpiresAt())
                .thenReturn(LocalDateTime.now().plusMinutes(10));

        when(booking.getId())
                .thenReturn(10L);

        when(booking.getFlight())
                .thenReturn(flight);

        when(booking.getSeatClass())
                .thenReturn(SeatClass.BUSINESS);

        when(flight.getId())
                .thenReturn(4L);

        when(passengerRepository.countByBookingId(10L))
                .thenReturn(2L);

        when(paymentGateway.processPayment(
                new BigDecimal("1500.00")
        )).thenReturn(PaymentResult.SUCCESS);

        when(paymentRepository.save(payment))
                .thenReturn(payment);

        when(paymentMapper.toResponse(payment))
                .thenReturn(expectedResponse);

        PaymentResponse result =
                paymentService.processPayment(1L);

        verify(payment)
                .setStatus(PaymentStatus.SUCCESS);

        verify(payment)
                .setProcessedAt(any(LocalDateTime.class));

        verify(flightService)
                .reserveSeats(
                        4L,
                        SeatClass.BUSINESS,
                        2
                );

        verify(booking)
                .setStatus(BookingStatus.CONFIRMED);

        verify(booking)
                .setTotalAmount(
                        new BigDecimal("1500.00")
                );

        verify(paymentRepository)
                .save(payment);

        verify(bookingRepository)
                .save(booking);

        verify(paymentMapper)
                .toResponse(payment);

        assertSame(expectedResponse, result);
    }

    @Test
    void processPayment_shouldMarkPaymentFailed_whenGatewayReturnsFailed() {

        Payment payment = mock(Payment.class);
        Booking booking = mock(Booking.class);
        PaymentResponse expectedResponse = mock(PaymentResponse.class);

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        when(payment.getStatus())
                .thenReturn(PaymentStatus.INITIATED);

        when(payment.getBooking())
                .thenReturn(booking);

        when(payment.getAmount())
                .thenReturn(new BigDecimal("500.00"));

        when(booking.getStatus())
                .thenReturn(BookingStatus.PENDING);

        when(booking.getExpiresAt())
                .thenReturn(LocalDateTime.now().plusMinutes(10));

        when(booking.getId())
                .thenReturn(10L);

        when(passengerRepository.countByBookingId(10L))
                .thenReturn(1L);

        when(paymentGateway.processPayment(
                new BigDecimal("500.00")
        )).thenReturn(PaymentResult.FAILED);

        when(paymentRepository.save(payment))
                .thenReturn(payment);

        when(paymentMapper.toResponse(payment))
                .thenReturn(expectedResponse);

        PaymentResponse result =
                paymentService.processPayment(1L);

        verify(payment)
                .setStatus(PaymentStatus.FAILED);

        verify(payment)
                .setProcessedAt(any(LocalDateTime.class));

        verify(paymentRepository)
                .save(payment);

        verify(paymentMapper)
                .toResponse(payment);

        verify(flightService, never())
                .reserveSeats(
                        anyLong(),
                        any(SeatClass.class),
                        anyInt()
                );

        verify(booking, never())
                .setStatus(BookingStatus.CONFIRMED);

        verify(bookingRepository, never())
                .save(any());

        assertSame(expectedResponse, result);
    }

    @Test
    void processPayment_shouldRejectWhenPaymentDoesNotExist() {

        when(paymentRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidPaymentException.class,
                () -> paymentService.processPayment(99L)
        );

        verifyNoInteractions(
                paymentGateway,
                flightService,
                passengerRepository,
                bookingRepository
        );
    }

    @Test
    void processPayment_shouldRejectAlreadyProcessedPayment() {

        Payment payment = mock(Payment.class);

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        when(payment.getStatus())
                .thenReturn(PaymentStatus.SUCCESS);

        InvalidPaymentException exception =
                assertThrows(
                        InvalidPaymentException.class,
                        () -> paymentService.processPayment(1L)
                );

        assertEquals(
                "Only an initiated payment can be processed",
                exception.getMessage()
        );

        verifyNoInteractions(
                paymentGateway,
                flightService,
                passengerRepository,
                bookingRepository
        );
    }

    @Test
    void processPayment_shouldRejectWhenBookingIsNotPending() {

        Payment payment = mock(Payment.class);
        Booking booking = mock(Booking.class);

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        when(payment.getStatus())
                .thenReturn(PaymentStatus.INITIATED);

        when(payment.getBooking())
                .thenReturn(booking);

        when(booking.getStatus())
                .thenReturn(BookingStatus.CANCELLED);

        assertThrows(
                InvalidPaymentException.class,
                () -> paymentService.processPayment(1L)
        );

        verifyNoInteractions(
                paymentGateway,
                flightService,
                passengerRepository
        );
    }

    @Test
    void processPayment_shouldRejectExpiredBooking() {

        Payment payment = mock(Payment.class);
        Booking booking = mock(Booking.class);

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        when(payment.getStatus())
                .thenReturn(PaymentStatus.INITIATED);

        when(payment.getBooking())
                .thenReturn(booking);

        when(booking.getStatus())
                .thenReturn(BookingStatus.PENDING);

        when(booking.getExpiresAt())
                .thenReturn(LocalDateTime.now().minusMinutes(1));

        assertThrows(
                InvalidPaymentException.class,
                () -> paymentService.processPayment(1L)
        );

        verifyNoInteractions(
                paymentGateway,
                flightService,
                passengerRepository
        );
    }

    @Test
    void processPayment_shouldRejectWhenBookingHasNoPassengers() {

        Payment payment = mock(Payment.class);
        Booking booking = mock(Booking.class);

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        when(payment.getStatus())
                .thenReturn(PaymentStatus.INITIATED);

        when(payment.getBooking())
                .thenReturn(booking);

        when(booking.getStatus())
                .thenReturn(BookingStatus.PENDING);

        when(booking.getExpiresAt())
                .thenReturn(LocalDateTime.now().plusMinutes(10));

        when(booking.getId())
                .thenReturn(10L);

        when(passengerRepository.countByBookingId(10L))
                .thenReturn(0L);

        assertThrows(
                InvalidPaymentException.class,
                () -> paymentService.processPayment(1L)
        );

        verifyNoInteractions(
                paymentGateway,
                flightService
        );
    }

    // ==================== REFUND ====================

    @Test
    void refundPayment_shouldMarkSuccessfulPaymentAsRefunded() {

        Payment payment = mock(Payment.class);

        when(paymentRepository.findByBookingIdAndStatus(
                10L,
                PaymentStatus.SUCCESS
        )).thenReturn(Optional.of(payment));

        paymentService.refundPayment(10L);

        verify(payment)
                .setStatus(PaymentStatus.REFUNDED);

        verify(payment)
                .setProcessedAt(any(LocalDateTime.class));

        verify(paymentRepository)
                .save(payment);
    }

    @Test
    void refundPayment_shouldRejectWhenNoSuccessfulPaymentExists() {

        when(paymentRepository.findByBookingIdAndStatus(
                10L,
                PaymentStatus.SUCCESS
        )).thenReturn(Optional.empty());

        InvalidPaymentException exception =
                assertThrows(
                        InvalidPaymentException.class,
                        () -> paymentService.refundPayment(10L)
                );

        assertEquals(
                "No successful payment found for booking id: 10",
                exception.getMessage()
        );

        verify(paymentRepository, never())
                .save(any());
    }
}