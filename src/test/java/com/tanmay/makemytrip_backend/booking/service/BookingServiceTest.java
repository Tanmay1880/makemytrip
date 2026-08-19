package com.tanmay.makemytrip_backend.booking.service;

import com.tanmay.makemytrip_backend.airline.repository.AirlineRepository;
import com.tanmay.makemytrip_backend.airport.repository.AirportRepository;
import com.tanmay.makemytrip_backend.booking.dto.BookingRequest;
import com.tanmay.makemytrip_backend.booking.dto.BookingResponse;
import com.tanmay.makemytrip_backend.booking.entity.Booking;
import com.tanmay.makemytrip_backend.booking.entity.BookingStatus;
import com.tanmay.makemytrip_backend.booking.entity.SeatClass;
import com.tanmay.makemytrip_backend.booking.exception.InvalidBookingException;
import com.tanmay.makemytrip_backend.booking.mapper.BookingMapper;
import com.tanmay.makemytrip_backend.booking.repository.BookingRepository;
import com.tanmay.makemytrip_backend.flight.entity.Flight;
import com.tanmay.makemytrip_backend.flight.exception.FlightNotFoundException;
import com.tanmay.makemytrip_backend.flight.repository.FlightRepository;
import com.tanmay.makemytrip_backend.flight.service.FlightService;
import com.tanmay.makemytrip_backend.passenger.repository.PassengerRepository;
import com.tanmay.makemytrip_backend.payment.service.PaymentService;
import com.tanmay.makemytrip_backend.booking.exception.BookingNotFoundException;
import com.tanmay.makemytrip_backend.user.entity.User;
import com.tanmay.makemytrip_backend.user.exception.UserNotFoundException;
import com.tanmay.makemytrip_backend.user.repository.UserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private FlightService flightService;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private BookingService bookingService;

    // ==================== CREATE ====================

    @Test
    void createBooking_success() {

        BookingRequest request = new BookingRequest();
        request.setUserId(1L);
        request.setFlightId(10L);
        request.setSeatClass(SeatClass.ECONOMY);

        User user = mock(User.class);
        Flight flight = mock(Flight.class);
        Booking booking = mock(Booking.class);
        BookingResponse response = mock(BookingResponse.class);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(user.getActive())
                .thenReturn(true);

        when(flightRepository.findByIdAndActiveTrue(10L))
                .thenReturn(Optional.of(flight));

        when(flight.getEconomySeatsAvailable())
                .thenReturn(10);

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        when(bookingMapper.toResponse(booking))
                .thenReturn(response);

        BookingResponse result =
                bookingService.createBooking(request);

        assertSame(response, result);

        verify(bookingRepository)
                .save(any(Booking.class));

        verify(bookingMapper)
                .toResponse(booking);
    }

    @Test
    void createBooking_userNotFound() {

        BookingRequest request = new BookingRequest();
        request.setUserId(99L);
        request.setFlightId(10L);
        request.setSeatClass(SeatClass.ECONOMY);

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> bookingService.createBooking(request)
        );

        verifyNoInteractions(flightRepository);
        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(bookingMapper);
    }

    @Test
    void createBooking_inactiveUser() {

        BookingRequest request = new BookingRequest();
        request.setUserId(1L);
        request.setFlightId(10L);
        request.setSeatClass(SeatClass.ECONOMY);

        User user = mock(User.class);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(user.getActive())
                .thenReturn(false);

        InvalidBookingException exception =
                assertThrows(
                        InvalidBookingException.class,
                        () -> bookingService.createBooking(request)
                );

        assertEquals(
                "Cannot create booking for an inactive user",
                exception.getMessage()
        );

        verifyNoInteractions(flightRepository);
        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(bookingMapper);
    }

    @Test
    void createBooking_flightNotFound() {

        BookingRequest request = new BookingRequest();
        request.setUserId(1L);
        request.setFlightId(99L);
        request.setSeatClass(SeatClass.ECONOMY);

        User user = mock(User.class);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(user.getActive())
                .thenReturn(true);

        when(flightRepository.findByIdAndActiveTrue(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                FlightNotFoundException.class,
                () -> bookingService.createBooking(request)
        );

        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(bookingMapper);
    }

    @Test
    void createBooking_noSeatsAvailable() {

        BookingRequest request = new BookingRequest();
        request.setUserId(1L);
        request.setFlightId(10L);
        request.setSeatClass(SeatClass.ECONOMY);

        User user = mock(User.class);
        Flight flight = mock(Flight.class);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(user.getActive())
                .thenReturn(true);

        when(flightRepository.findByIdAndActiveTrue(10L))
                .thenReturn(Optional.of(flight));

        when(flight.getEconomySeatsAvailable())
                .thenReturn(0);

        InvalidBookingException exception =
                assertThrows(
                        InvalidBookingException.class,
                        () -> bookingService.createBooking(request)
                );

        assertEquals(
                "No seats available in " + SeatClass.ECONOMY,
                exception.getMessage()
        );

        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(bookingMapper);
    }

    // ==================== CANCEL ====================

    @Test
    void cancelBooking_confirmedBooking_shouldReleaseSeatsAndRefundPayment() {

        Booking booking = mock(Booking.class);
        Flight flight = mock(Flight.class);
        BookingResponse response = mock(BookingResponse.class);

        when(bookingRepository.findById(13L))
                .thenReturn(Optional.of(booking));

        when(booking.getId())
                .thenReturn(13L);

        when(booking.getStatus())
                .thenReturn(BookingStatus.CONFIRMED);

        when(booking.getSeatClass())
                .thenReturn(SeatClass.PREMIUM_ECONOMY);

        when(booking.getFlight())
                .thenReturn(flight);

        when(flight.getId())
                .thenReturn(4L);

        when(passengerRepository.countByBookingId(13L))
                .thenReturn(1L);

        when(bookingRepository.save(booking))
                .thenReturn(booking);

        when(bookingMapper.toResponse(booking))
                .thenReturn(response);

        BookingResponse result =
                bookingService.cancelBooking(13L);

        verify(flightService).releaseSeats(
                4L,
                SeatClass.PREMIUM_ECONOMY,
                1
        );

        verify(paymentService)
                .refundPayment(13L);

        verify(booking)
                .setStatus(BookingStatus.CANCELLED);

        verify(booking)
                .setCancelledAt(any(LocalDateTime.class));

        verify(bookingRepository)
                .save(booking);

        assertSame(response, result);
    }

    @Test
    void cancelBooking_bookingNotFound_shouldThrowException() {

        when(bookingRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.cancelBooking(99L)
        );

        verifyNoInteractions(
                flightService,
                paymentService,
                passengerRepository,
                bookingMapper
        );

        verify(bookingRepository, never())
                .save(any());
    }

    @Test
    void cancelBooking_alreadyCancelled_shouldThrowException() {

        Booking booking = mock(Booking.class);

        when(bookingRepository.findById(13L))
                .thenReturn(Optional.of(booking));

        when(booking.getStatus())
                .thenReturn(BookingStatus.CANCELLED);

        InvalidBookingException exception =
                assertThrows(
                        InvalidBookingException.class,
                        () -> bookingService.cancelBooking(13L)
                );

        assertEquals(
                "Booking is already cancelled",
                exception.getMessage()
        );

        verifyNoInteractions(
                flightService,
                paymentService,
                passengerRepository,
                bookingMapper
        );

        verify(bookingRepository, never())
                .save(any());
    }

    @Test
    void cancelBooking_expiredBooking_shouldThrowException() {

        Booking booking = mock(Booking.class);

        when(bookingRepository.findById(13L))
                .thenReturn(Optional.of(booking));

        when(booking.getStatus())
                .thenReturn(BookingStatus.EXPIRED);

        InvalidBookingException exception =
                assertThrows(
                        InvalidBookingException.class,
                        () -> bookingService.cancelBooking(13L)
                );

        assertEquals(
                "Expired booking cannot be cancelled",
                exception.getMessage()
        );

        verifyNoInteractions(
                flightService,
                paymentService,
                passengerRepository,
                bookingMapper
        );

        verify(bookingRepository, never())
                .save(any());
    }

    @Test
    void cancelBooking_confirmedBookingWithoutPassengers_shouldThrowException() {

        Booking booking = mock(Booking.class);

        when(bookingRepository.findById(13L))
                .thenReturn(Optional.of(booking));

        when(booking.getId())
                .thenReturn(13L);

        when(booking.getStatus())
                .thenReturn(BookingStatus.CONFIRMED);

        when(passengerRepository.countByBookingId(13L))
                .thenReturn(0L);

        InvalidBookingException exception =
                assertThrows(
                        InvalidBookingException.class,
                        () -> bookingService.cancelBooking(13L)
                );

        assertEquals(
                "Confirmed booking has no passengers",
                exception.getMessage()
        );

        verifyNoInteractions(
                flightService,
                paymentService,
                bookingMapper
        );

        verify(bookingRepository, never())
                .save(any());
    }

    // ==================== EXPIRATION ====================

    @Test
    void expirePendingBookings_shouldMarkBookingsAsExpired() {

        Booking firstBooking = mock(Booking.class);
        Booking secondBooking = mock(Booking.class);

        when(bookingRepository
                .findByStatusAndExpiresAtBefore(
                        eq(BookingStatus.PENDING),
                        any(LocalDateTime.class)
                ))
                .thenReturn(List.of(firstBooking, secondBooking));

        bookingService.expirePendingBookings();

        verify(firstBooking)
                .setStatus(BookingStatus.EXPIRED);

        verify(secondBooking)
                .setStatus(BookingStatus.EXPIRED);

        verify(bookingRepository)
                .saveAll(List.of(firstBooking, secondBooking));
    }

    @Test
    void expirePendingBookings_whenNoExpiredBookings_shouldSaveEmptyList() {

        when(bookingRepository
                .findByStatusAndExpiresAtBefore(
                        eq(BookingStatus.PENDING),
                        any(LocalDateTime.class)
                ))
                .thenReturn(List.of());

        bookingService.expirePendingBookings();

        verify(bookingRepository)
                .saveAll(List.of());

        verifyNoInteractions(
                bookingMapper,
                flightService,
                paymentService,
                passengerRepository
        );
    }
}