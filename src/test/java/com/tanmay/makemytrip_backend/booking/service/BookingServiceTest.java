package com.tanmay.makemytrip_backend.booking.service;

import com.tanmay.makemytrip_backend.booking.dto.BookingRequest;
import com.tanmay.makemytrip_backend.booking.dto.BookingResponse;
import com.tanmay.makemytrip_backend.booking.entity.Booking;
import com.tanmay.makemytrip_backend.booking.entity.BookingStatus;
import com.tanmay.makemytrip_backend.booking.entity.SeatClass;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
        request.setFlightId(1L);
        request.setSeatClass(SeatClass.ECONOMY);

        User user = mock(User.class);
        Flight flight = mock(Flight.class);

        Booking booking = mock(Booking.class);
        BookingResponse response = mock(BookingResponse.class);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(user.getActive())
                .thenReturn(true);

        when(flightRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(flight));

        when(flight.getAvailableSeats(SeatClass.ECONOMY))
                .thenReturn(10);

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        when(bookingMapper.toResponse(booking))
                .thenReturn(response);

        BookingResponse result =
                bookingService.createBooking(request);

        assertSame(response, result);

        verify(userRepository)
                .findById(1L);

        verify(flightRepository)
                .findByIdAndActiveTrue(1L);

        verify(flight)
                .getAvailableSeats(SeatClass.ECONOMY);

        verify(bookingRepository)
                .save(any(Booking.class));

        verify(bookingMapper)
                .toResponse(booking);
    }

    @Test
    void createBooking_rejectsUnknownUser() {

        BookingRequest request = new BookingRequest();

        request.setUserId(99L);
        request.setFlightId(1L);
        request.setSeatClass(SeatClass.ECONOMY);

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> bookingService.createBooking(request)
        );

        verify(userRepository)
                .findById(99L);

        verifyNoInteractions(flightRepository);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void createBooking_rejectsInactiveUser() {

        BookingRequest request = new BookingRequest();

        request.setUserId(1L);
        request.setFlightId(1L);
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

        verify(userRepository)
                .findById(1L);

        verify(user)
                .getActive();

        verifyNoInteractions(flightRepository);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void createBooking_rejectsUnknownFlight() {

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

        verify(flightRepository)
                .findByIdAndActiveTrue(99L);

        verifyNoInteractions(bookingRepository);
    }

    @Test
    void createBooking_noSeatsAvailable() {

        BookingRequest request = new BookingRequest();

        request.setUserId(1L);
        request.setFlightId(1L);
        request.setSeatClass(SeatClass.ECONOMY);

        User user = mock(User.class);
        Flight flight = mock(Flight.class);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(user.getActive())
                .thenReturn(true);

        when(flightRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(flight));

        when(flight.getAvailableSeats(SeatClass.ECONOMY))
                .thenReturn(0);

        InvalidBookingException exception =
                assertThrows(
                        InvalidBookingException.class,
                        () -> bookingService.createBooking(request)
                );

        assertEquals(
                "No seats available in ECONOMY",
                exception.getMessage()
        );

        verify(flight)
                .getAvailableSeats(SeatClass.ECONOMY);

        verifyNoInteractions(bookingRepository);
    }

    // ==================== GET BY ID ====================

    @Test
    void getBookingById_success() {

        Booking booking = mock(Booking.class);
        BookingResponse response = mock(BookingResponse.class);

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(bookingMapper.toResponse(booking))
                .thenReturn(response);

        BookingResponse result =
                bookingService.getBookingById(1L);

        assertSame(response, result);
    }

    @Test
    void getBookingById_notFound() {

        when(bookingRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.getBookingById(99L)
        );
    }

    // ==================== GET ALL ====================

    @Test
    void getAllBookings_success() {

        Booking booking1 = mock(Booking.class);
        Booking booking2 = mock(Booking.class);

        BookingResponse response1 = mock(BookingResponse.class);
        BookingResponse response2 = mock(BookingResponse.class);

        when(bookingRepository.findAll())
                .thenReturn(List.of(booking1, booking2));

        when(bookingMapper.toResponse(booking1))
                .thenReturn(response1);

        when(bookingMapper.toResponse(booking2))
                .thenReturn(response2);

        List<BookingResponse> result =
                bookingService.getAllBookings();

        assertEquals(2, result.size());
        assertSame(response1, result.get(0));
        assertSame(response2, result.get(1));
    }

    // ==================== CANCEL ====================

    @Test
    void cancelBooking_rejectsAlreadyCancelled() {

        Booking booking = mock(Booking.class);

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(booking.getStatus())
                .thenReturn(BookingStatus.CANCELLED);

        assertThrows(
                InvalidBookingException.class,
                () -> bookingService.cancelBooking(1L)
        );

        verifyNoInteractions(flightService);
        verifyNoInteractions(paymentService);
    }

    @Test
    void cancelBooking_rejectsExpiredBooking() {

        Booking booking = mock(Booking.class);

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(booking.getStatus())
                .thenReturn(BookingStatus.EXPIRED);

        assertThrows(
                InvalidBookingException.class,
                () -> bookingService.cancelBooking(1L)
        );

        verifyNoInteractions(flightService);
        verifyNoInteractions(paymentService);
    }

    @Test
    void cancelBooking_releasesSeatsAndRefundsConfirmedBooking() {

        Booking booking = mock(Booking.class);
        Flight flight = mock(Flight.class);

        BookingResponse response = mock(BookingResponse.class);

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(booking.getStatus())
                .thenReturn(BookingStatus.CONFIRMED);

        when(booking.getId())
                .thenReturn(1L);

        when(booking.getFlight())
                .thenReturn(flight);

        when(flight.getId())
                .thenReturn(10L);

        when(booking.getSeatClass())
                .thenReturn(SeatClass.ECONOMY);

        when(passengerRepository.countByBookingId(1L))
                .thenReturn(2L);

        when(bookingRepository.save(booking))
                .thenReturn(booking);

        when(bookingMapper.toResponse(booking))
                .thenReturn(response);

        BookingResponse result =
                bookingService.cancelBooking(1L);

        assertSame(response, result);

        verify(flightService)
                .releaseSeats(
                        10L,
                        SeatClass.ECONOMY,
                        2
                );

        verify(paymentService)
                .refundPayment(1L);

        verify(booking)
                .setStatus(BookingStatus.CANCELLED);

        verify(booking)
                .setCancelledAt(any(LocalDateTime.class));

        verify(bookingRepository)
                .save(booking);
    }

    @Test
    void cancelBooking_rejectsConfirmedBookingWithoutPassengers() {

        Booking booking = mock(Booking.class);

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(booking.getStatus())
                .thenReturn(BookingStatus.CONFIRMED);

        when(booking.getId())
                .thenReturn(1L);

        when(passengerRepository.countByBookingId(1L))
                .thenReturn(0L);

        assertThrows(
                InvalidBookingException.class,
                () -> bookingService.cancelBooking(1L)
        );

        verifyNoInteractions(flightService);
        verifyNoInteractions(paymentService);
        verify(bookingRepository, never())
                .save(any());
    }

    // ==================== EXPIRE ====================

    @Test
    void expirePendingBookings_marksBookingsAsExpired() {

        Booking booking1 = mock(Booking.class);
        Booking booking2 = mock(Booking.class);

        when(bookingRepository.findByStatusAndExpiresAtBefore(
                eq(BookingStatus.PENDING),
                any(LocalDateTime.class)
        )).thenReturn(List.of(booking1, booking2));

        bookingService.expirePendingBookings();

        verify(booking1)
                .setStatus(BookingStatus.EXPIRED);

        verify(booking2)
                .setStatus(BookingStatus.EXPIRED);

        verify(bookingRepository)
                .saveAll(List.of(booking1, booking2));
    }

    @Test
    void expirePendingBookings_doesNothingWhenNoBookings() {

        when(bookingRepository.findByStatusAndExpiresAtBefore(
                eq(BookingStatus.PENDING),
                any(LocalDateTime.class)
        )).thenReturn(List.of());

        bookingService.expirePendingBookings();

        verify(bookingRepository)
                .saveAll(List.of());
    }
}