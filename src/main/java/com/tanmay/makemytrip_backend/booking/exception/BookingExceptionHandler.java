package com.tanmay.makemytrip_backend.booking.exception;

import com.tanmay.makemytrip_backend.common.exception.ApiErrorResponse;
import com.tanmay.makemytrip_backend.booking.controller.BookingController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.tanmay.makemytrip_backend.flight.exception.FlightNotFoundException;
import com.tanmay.makemytrip_backend.user.exception.UserNotFoundException;

import java.time.LocalDateTime;

@RestControllerAdvice(assignableTypes = BookingController.class)
public class BookingExceptionHandler {

    // ==================== BOOKING NOT FOUND ====================

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleBookingNotFound(
            BookingNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    // ==================== INVALID BOOKING ====================

    @ExceptionHandler(InvalidBookingException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidBooking(
            InvalidBookingException exception) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
    }

    // ==================== USER NOT FOUND ====================

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFound(
            UserNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

// ==================== FLIGHT NOT FOUND ====================

    @ExceptionHandler(FlightNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleFlightNotFound(
            FlightNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    // ==================== RESPONSE BUILDER ====================

    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            String message) {

        ApiErrorResponse response = new ApiErrorResponse(
                status.value(),
                message,
                LocalDateTime.now(),
                null
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }
}