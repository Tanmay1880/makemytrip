package com.tanmay.makemytrip_backend.common.exception;

import com.tanmay.makemytrip_backend.airline.exception.AirlineAlreadyExistsException;
import com.tanmay.makemytrip_backend.airline.exception.AirlineNotFoundException;
import com.tanmay.makemytrip_backend.airport.exception.AirportAlreadyExistsException;
import com.tanmay.makemytrip_backend.airport.exception.AirportNotFoundException;
import com.tanmay.makemytrip_backend.booking.exception.BookingNotFoundException;
import com.tanmay.makemytrip_backend.booking.exception.InvalidBookingException;
import com.tanmay.makemytrip_backend.flight.exception.FlightAlreadyExistsException;
import com.tanmay.makemytrip_backend.flight.exception.FlightNotFoundException;
import com.tanmay.makemytrip_backend.flight.exception.InvalidFlightException;
import com.tanmay.makemytrip_backend.payment.exception.InvalidPaymentException;
import com.tanmay.makemytrip_backend.user.exception.UserAlreadyExistsException;
import com.tanmay.makemytrip_backend.user.exception.UserNotFoundException;
import org.springframework.dao.OptimisticLockingFailureException;
import com.tanmay.makemytrip_backend.passenger.exception.InvalidPassengerException;
import com.tanmay.makemytrip_backend.passenger.exception.PassengerNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==================== VALIDATION ====================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                errors
        );
    }

    // ==================== AIRLINE ====================

    @ExceptionHandler(AirlineNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleAirlineNotFound(
            AirlineNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(AirlineAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleAirlineAlreadyExists(
            AirlineAlreadyExistsException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    // ==================== AIRPORT ====================

    @ExceptionHandler(AirportNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleAirportNotFound(
            AirportNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(AirportAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleAirportAlreadyExists(
            AirportAlreadyExistsException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    // ==================== FLIGHT ====================

    @ExceptionHandler(FlightNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleFlightNotFound(
            FlightNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(FlightAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleFlightAlreadyExists(
            FlightAlreadyExistsException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    @ExceptionHandler(InvalidFlightException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidFlight(
            InvalidFlightException exception) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
    }

    // ==================== PASSENGER ====================

    @ExceptionHandler(PassengerNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handlePassengerNotFound(
            PassengerNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(InvalidPassengerException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidPassenger(
            InvalidPassengerException exception) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
    }

    // ==================== BOOKING ====================

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleBookingNotFound(
            BookingNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(InvalidBookingException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidBooking(
            InvalidBookingException exception) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
    }

    // ==================== PAYMENT ====================

    @ExceptionHandler(InvalidPaymentException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidPayment(
            InvalidPaymentException exception) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
    }

    // ==================== USER ====================

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFound(
            UserNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleUserAlreadyExists(
            UserAlreadyExistsException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    // ==================== CONCURRENCY ====================

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ApiErrorResponse> handleOptimisticLockingFailure(
            OptimisticLockingFailureException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                "The resource was modified by another request. Please try again."
        );
    }

    // ==================== UNEXPECTED ====================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(
            Exception exception) {

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );
    }

    // ==================== RESPONSE BUILDERS ====================

    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            String message) {

        return buildResponse(
                status,
                message,
                null
        );
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            Map<String, String> errors) {

        ApiErrorResponse response = new ApiErrorResponse(
                status.value(),
                message,
                LocalDateTime.now(),
                errors
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }
}