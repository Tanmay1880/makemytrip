package com.tanmay.makemytrip_backend.passenger.service;

import com.tanmay.makemytrip_backend.booking.entity.Booking;
import com.tanmay.makemytrip_backend.booking.entity.BookingStatus;
import com.tanmay.makemytrip_backend.booking.exception.BookingNotFoundException;
import com.tanmay.makemytrip_backend.booking.repository.BookingRepository;
import com.tanmay.makemytrip_backend.passenger.dto.PassengerRequest;
import com.tanmay.makemytrip_backend.passenger.dto.PassengerResponse;
import com.tanmay.makemytrip_backend.passenger.entity.Passenger;
import com.tanmay.makemytrip_backend.passenger.exception.InvalidPassengerException;
import com.tanmay.makemytrip_backend.passenger.exception.PassengerNotFoundException;
import com.tanmay.makemytrip_backend.passenger.mapper.PassengerMapper;
import com.tanmay.makemytrip_backend.passenger.repository.PassengerRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PassengerService {

    private final PassengerRepository passengerRepository;
    private final PassengerMapper passengerMapper;
    private final BookingRepository bookingRepository;

    public PassengerService(
            PassengerRepository passengerRepository,
            PassengerMapper passengerMapper,
            BookingRepository bookingRepository) {

        this.passengerRepository = passengerRepository;
        this.passengerMapper = passengerMapper;
        this.bookingRepository = bookingRepository;
    }

    // ==================== CREATE ====================

    public PassengerResponse createPassenger(
            Long bookingId,
            PassengerRequest request) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new BookingNotFoundException(
                                "Booking not found with id: " + bookingId
                        )
                );

        validateBookingAccess(booking);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new InvalidPassengerException(
                    "Passengers can only be added to a pending booking"
            );
        }

        Passenger passenger = new Passenger(
                booking,
                request.getFirstName(),
                request.getLastName(),
                request.getDateOfBirth(),
                request.getGender(),
                request.getPassengerType()
        );

        Passenger savedPassenger = passengerRepository.save(passenger);

        return passengerMapper.toResponse(savedPassenger);
    }

    // ==================== GET BY ID ====================

    public PassengerResponse getPassengerById(Long id) {

        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() ->
                        new PassengerNotFoundException(
                                "Passenger not found with id: " + id
                        )
                );

        if (!hasBookingAccess(passenger.getBooking())) {
            throw new PassengerNotFoundException(
                    "Passenger not found with id: " + id
            );
        }

        return passengerMapper.toResponse(passenger);
    }

    // ==================== GET BY BOOKING ====================

    public List<PassengerResponse> getPassengersByBookingId(
            Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new BookingNotFoundException(
                                "Booking not found with id: " + bookingId
                        )
                );

        validateBookingAccess(booking);

        return passengerRepository.findByBookingId(bookingId)
                .stream()
                .map(passengerMapper::toResponse)
                .toList();
    }

    // ==================== AUTHORIZATION ====================

    private void validateBookingAccess(Booking booking) {

        if (!hasBookingAccess(booking)) {
            throw new BookingNotFoundException(
                    "Booking not found with id: " + booking.getId()
            );
        }
    }

    private boolean hasBookingAccess(Booking booking) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                );

        if (isAdmin) {
            return true;
        }

        return booking.getUser().getEmail()
                .equals(authentication.getName());
    }
}