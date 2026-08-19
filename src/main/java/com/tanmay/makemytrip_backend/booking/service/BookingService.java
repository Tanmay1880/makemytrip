package com.tanmay.makemytrip_backend.booking.service;

import com.tanmay.makemytrip_backend.booking.dto.BookingRequest;
import com.tanmay.makemytrip_backend.booking.dto.BookingResponse;
import com.tanmay.makemytrip_backend.booking.entity.Booking;
import com.tanmay.makemytrip_backend.booking.entity.BookingStatus;
import com.tanmay.makemytrip_backend.booking.exception.BookingNotFoundException;
import com.tanmay.makemytrip_backend.booking.exception.InvalidBookingException;
import com.tanmay.makemytrip_backend.booking.mapper.BookingMapper;
import com.tanmay.makemytrip_backend.booking.repository.BookingRepository;
import com.tanmay.makemytrip_backend.flight.entity.Flight;
import com.tanmay.makemytrip_backend.flight.exception.FlightNotFoundException;
import com.tanmay.makemytrip_backend.flight.repository.FlightRepository;
import com.tanmay.makemytrip_backend.flight.service.FlightService;
import com.tanmay.makemytrip_backend.passenger.repository.PassengerRepository;
import com.tanmay.makemytrip_backend.payment.service.PaymentService;
import com.tanmay.makemytrip_backend.user.entity.User;
import com.tanmay.makemytrip_backend.user.exception.UserNotFoundException;
import com.tanmay.makemytrip_backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final FlightRepository flightRepository;
    private final PassengerRepository passengerRepository;
    private final FlightService flightService;
    private final PaymentService paymentService;

    public BookingService(
            BookingRepository bookingRepository,
            BookingMapper bookingMapper,
            UserRepository userRepository,
            FlightRepository flightRepository,
            PassengerRepository passengerRepository,
            FlightService flightService,
            PaymentService paymentService) {

        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.userRepository = userRepository;
        this.flightRepository = flightRepository;
        this.passengerRepository = passengerRepository;
        this.flightService = flightService;
        this.paymentService = paymentService;
    }

    // ==================== CREATE ====================

    public BookingResponse createBooking(BookingRequest request) {

        // ==================== USER VALIDATION ====================

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: " + request.getUserId()
                        )
                );

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new InvalidBookingException(
                    "Cannot create booking for an inactive user"
            );
        }

        // ==================== FLIGHT VALIDATION ====================

        Flight flight = flightRepository.findByIdAndActiveTrue(
                        request.getFlightId()
                )
                .orElseThrow(() ->
                        new FlightNotFoundException(
                                "Active flight not found with id: "
                                        + request.getFlightId()
                        )
                );

        // ==================== SEAT AVAILABILITY ====================

        int availableSeats;

        switch (request.getSeatClass()) {

            case ECONOMY -> {
                availableSeats =
                        flight.getEconomySeatsAvailable();
            }

            case PREMIUM_ECONOMY -> {
                availableSeats =
                        flight.getPremiumEconomySeatsAvailable();
            }

            case BUSINESS -> {
                availableSeats =
                        flight.getBusinessSeatsAvailable();
            }

            default -> throw new InvalidBookingException(
                    "Invalid seat class"
            );
        }

        if (availableSeats <= 0) {
            throw new InvalidBookingException(
                    "No seats available in " + request.getSeatClass()
            );
        }

        // ==================== BOOKING DETAILS ====================

        String pnr = generatePnr();

        LocalDateTime expiresAt = LocalDateTime.now()
                .plusMinutes(15);

        BigDecimal totalAmount = BigDecimal.ZERO;

        // ==================== CREATE BOOKING ====================

        Booking booking = new Booking(
                pnr,
                user,
                flight,
                request.getSeatClass(),
                BookingStatus.PENDING,
                totalAmount,
                expiresAt
        );

        Booking savedBooking = bookingRepository.save(booking);

        return bookingMapper.toResponse(savedBooking);
    }

    // ==================== GET BY ID ====================

    public BookingResponse getBookingById(Long id) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new BookingNotFoundException(
                                "Booking not found with id: " + id
                        )
                );

        return bookingMapper.toResponse(booking);
    }

    // ==================== GET ALL ====================

    public List<BookingResponse> getAllBookings() {

        return bookingRepository.findAll()
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    // ==================== CANCEL ====================

    @Transactional
    public BookingResponse cancelBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new BookingNotFoundException(
                                "Booking not found with id: " + bookingId
                        )
                );

        // ==================== STATUS VALIDATION ====================

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new InvalidBookingException(
                    "Booking is already cancelled"
            );
        }

        if (booking.getStatus() == BookingStatus.EXPIRED) {
            throw new InvalidBookingException(
                    "Expired booking cannot be cancelled"
            );
        }

        // ==================== CONFIRMED BOOKING ====================

        if (booking.getStatus() == BookingStatus.CONFIRMED) {

            long passengerCount =
                    passengerRepository.countByBookingId(
                            booking.getId()
                    );

            if (passengerCount == 0) {
                throw new InvalidBookingException(
                        "Confirmed booking has no passengers"
                );
            }

            // ==================== RELEASE SEATS ====================

            flightService.releaseSeats(
                    booking.getFlight().getId(),
                    booking.getSeatClass(),
                    (int) passengerCount
            );

            // ==================== REFUND PAYMENT ====================

            paymentService.refundPayment(booking.getId());
        }

        // ==================== CANCEL BOOKING ====================

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());

        Booking cancelledBooking =
                bookingRepository.save(booking);

        return bookingMapper.toResponse(cancelledBooking);
    }

    // ==================== EXPIRE PENDING BOOKINGS ====================

    @Transactional
    public void expirePendingBookings() {

        List<Booking> expiredBookings =
                bookingRepository.findByStatusAndExpiresAtBefore(
                        BookingStatus.PENDING,
                        LocalDateTime.now()
                );

        for (Booking booking : expiredBookings) {
            booking.setStatus(BookingStatus.EXPIRED);
        }

        bookingRepository.saveAll(expiredBookings);
    }

    // ==================== PNR ====================

    private String generatePnr() {

        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 10)
                .toUpperCase();
    }
}