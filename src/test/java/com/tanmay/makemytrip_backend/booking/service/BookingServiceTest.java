package com.tanmay.makemytrip_backend.booking.service;

import com.tanmay.makemytrip_backend.booking.dto.BookingResponse;
import com.tanmay.makemytrip_backend.booking.entity.Booking;
import com.tanmay.makemytrip_backend.booking.entity.BookingStatus;
import com.tanmay.makemytrip_backend.booking.entity.SeatClass;
import com.tanmay.makemytrip_backend.booking.mapper.BookingMapper;
import com.tanmay.makemytrip_backend.booking.repository.BookingRepository;
import com.tanmay.makemytrip_backend.flight.entity.Flight;
import com.tanmay.makemytrip_backend.flight.service.FlightService;
import com.tanmay.makemytrip_backend.passenger.repository.PassengerRepository;
import com.tanmay.makemytrip_backend.payment.service.PaymentService;
import com.tanmay.makemytrip_backend.user.repository.UserRepository;
import com.tanmay.makemytrip_backend.flight.repository.FlightRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @Test
    void cancelBooking_shouldReleaseSeatsAndRefundPayment() {

        // ==================== MOCK DEPENDENCIES ====================

        BookingRepository bookingRepository =
                mock(BookingRepository.class);

        BookingMapper bookingMapper =
                mock(BookingMapper.class);

        UserRepository userRepository =
                mock(UserRepository.class);

        FlightRepository flightRepository =
                mock(FlightRepository.class);

        PassengerRepository passengerRepository =
                mock(PassengerRepository.class);

        FlightService flightService =
                mock(FlightService.class);

        PaymentService paymentService =
                mock(PaymentService.class);

        // ==================== CREATE SERVICE ====================

        BookingService bookingService = new BookingService(
                bookingRepository,
                bookingMapper,
                userRepository,
                flightRepository,
                passengerRepository,
                flightService,
                paymentService
        );

        // ==================== TEST DATA ====================

        Booking booking = mock(Booking.class);

        Flight flight = mock(Flight.class);

        when(flight.getId())
                .thenReturn(4L);

        when(booking.getId())
                .thenReturn(13L);

        when(booking.getStatus())
                .thenReturn(BookingStatus.CONFIRMED);

        when(booking.getSeatClass())
                .thenReturn(SeatClass.PREMIUM_ECONOMY);

        when(booking.getFlight())
                .thenReturn(flight);

        when(passengerRepository.countByBookingId(13L))
                .thenReturn(1L);

        when(bookingRepository.findById(13L))
                .thenReturn(Optional.of(booking));

        BookingResponse expectedResponse =
                mock(BookingResponse.class);

        when(bookingMapper.toResponse(booking))
                .thenReturn(expectedResponse);

        when(bookingRepository.save(booking))
                .thenReturn(booking);

        // ==================== EXECUTE ====================

        BookingResponse result =
                bookingService.cancelBooking(13L);

        // ==================== VERIFY SEAT RELEASE ====================

        verify(flightService).releaseSeats(
                anyLong(),
                eq(SeatClass.PREMIUM_ECONOMY),
                eq(1)
        );

        // ==================== VERIFY REFUND ====================

        verify(paymentService)
                .refundPayment(13L);

        // ==================== VERIFY BOOKING CANCELLATION ====================

        verify(booking)
                .setStatus(BookingStatus.CANCELLED);

        verify(booking)
                .setCancelledAt(any());

        verify(bookingRepository)
                .save(booking);

        // ==================== VERIFY RESPONSE ====================

        assertEquals(expectedResponse, result);
    }
}