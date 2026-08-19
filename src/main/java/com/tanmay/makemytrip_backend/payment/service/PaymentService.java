package com.tanmay.makemytrip_backend.payment.service;

import com.tanmay.makemytrip_backend.booking.entity.Booking;
import com.tanmay.makemytrip_backend.booking.entity.BookingStatus;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final FlightService flightService;
    private final PaymentGateway paymentGateway;

    public PaymentService(
            PaymentRepository paymentRepository,
            PaymentMapper paymentMapper,
            BookingRepository bookingRepository,
            PassengerRepository passengerRepository,
            FlightService flightService,
            PaymentGateway paymentGateway) {

        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.flightService = flightService;
        this.paymentGateway = paymentGateway;
    }

    // ==================== CREATE PAYMENT ====================

    public PaymentResponse createPayment(PaymentRequest request) {

        // ==================== BOOKING VALIDATION ====================

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() ->
                        new BookingNotFoundException(
                                "Booking not found with id: "
                                        + request.getBookingId()
                        )
                );

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new InvalidPaymentException(
                    "Payment can only be initiated for a pending booking"
            );
        }

        validateBookingNotExpired(booking);

        // ==================== PASSENGER VALIDATION ====================

        long passengerCount =
                passengerRepository.countByBookingId(booking.getId());

        if (passengerCount == 0) {
            throw new InvalidPaymentException(
                    "Cannot initiate payment without passengers"
            );
        }

        // ==================== GET FLIGHT ====================

        Flight flight = booking.getFlight();

        // ==================== GET PRICE ====================

        BigDecimal pricePerPassenger =
                flight.getPrice(booking.getSeatClass());

        // ==================== CALCULATE TOTAL ====================

        BigDecimal totalAmount = pricePerPassenger.multiply(
                BigDecimal.valueOf(passengerCount)
        );

        // ==================== SUCCESS PAYMENT CHECK ====================

        boolean successfulPaymentExists =
                paymentRepository.findByBookingIdAndStatus(
                        booking.getId(),
                        PaymentStatus.SUCCESS
                ).isPresent();

        if (successfulPaymentExists) {
            throw new InvalidPaymentException(
                    "Booking already has a successful payment"
            );
        }

        // ==================== CREATE PAYMENT ====================

        String paymentReference = generatePaymentReference();

        Payment payment = new Payment(
                booking,
                totalAmount,
                PaymentStatus.INITIATED,
                paymentReference
        );

        Payment savedPayment = paymentRepository.save(payment);

        return paymentMapper.toResponse(savedPayment);
    }

    // ==================== PROCESS PAYMENT ====================

    @Transactional
    public PaymentResponse processPayment(Long paymentId) {

        // ==================== FIND PAYMENT ====================

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new InvalidPaymentException(
                                "Payment not found with id: " + paymentId
                        )
                );

        // ==================== PAYMENT STATUS ====================

        if (payment.getStatus() != PaymentStatus.INITIATED) {
            throw new InvalidPaymentException(
                    "Only an initiated payment can be processed"
            );
        }

        // ==================== BOOKING STATUS ====================

        Booking booking = payment.getBooking();

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new InvalidPaymentException(
                    "Booking cannot be confirmed"
            );
        }

        // ==================== BOOKING EXPIRATION ====================

        validateBookingNotExpired(booking);

        // ==================== PASSENGER VALIDATION ====================

        long passengerCount =
                passengerRepository.countByBookingId(booking.getId());

        if (passengerCount == 0) {
            throw new InvalidPaymentException(
                    "Cannot process payment without passengers"
            );
        }

        // ==================== PAYMENT GATEWAY ====================

        PaymentResult paymentResult =
                paymentGateway.processPayment(payment.getAmount());

        if (paymentResult == PaymentResult.FAILED) {

            payment.setStatus(PaymentStatus.FAILED);
            payment.setProcessedAt(LocalDateTime.now());

            Payment failedPayment = paymentRepository.save(payment);

            return paymentMapper.toResponse(failedPayment);
        }

        // ==================== RESERVE SEATS ====================

        flightService.reserveSeats(
                booking.getFlight().getId(),
                booking.getSeatClass(),
                (int) passengerCount
        );

        // ==================== PAYMENT SUCCESS ====================

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setProcessedAt(LocalDateTime.now());

        // ==================== BOOKING CONFIRMATION ====================

        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setTotalAmount(payment.getAmount());

        Payment savedPayment = paymentRepository.save(payment);

        bookingRepository.save(booking);

        return paymentMapper.toResponse(savedPayment);
    }

    // ==================== PAYMENT REFERENCE ====================

    private String generatePaymentReference() {

        return "PAY-" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 12)
                        .toUpperCase();
    }

    // ==================== EXPIRATION VALIDATION ====================

    private void validateBookingNotExpired(Booking booking) {

        if (!booking.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new InvalidPaymentException(
                    "Booking has expired"
            );
        }
    }

    // ==================== REFUND PAYMENT ====================

    @Transactional
    public void refundPayment(Long bookingId) {

        Payment payment =
                paymentRepository.findByBookingIdAndStatus(
                                bookingId,
                                PaymentStatus.SUCCESS
                        )
                        .orElseThrow(() ->
                                new InvalidPaymentException(
                                        "No successful payment found for booking id: "
                                                + bookingId
                                )
                        );

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setProcessedAt(LocalDateTime.now());

        paymentRepository.save(payment);
    }
}