package com.tanmay.makemytrip_backend.passenger.exception;

import com.tanmay.makemytrip_backend.booking.exception.BookingNotFoundException;
import com.tanmay.makemytrip_backend.common.exception.ApiErrorResponse;
import com.tanmay.makemytrip_backend.passenger.controller.PassengerController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(assignableTypes = PassengerController.class)
public class PassengerExceptionHandler {

    // ==================== PASSENGER NOT FOUND ====================

    @ExceptionHandler(PassengerNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handlePassengerNotFound(
            PassengerNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                null
        );
    }

    // ==================== BOOKING NOT FOUND ====================

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleBookingNotFound(
            BookingNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                null
        );
    }

    // ==================== INVALID PASSENGER ====================

    @ExceptionHandler(InvalidPassengerException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidPassenger(
            InvalidPassengerException exception) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                null
        );
    }

    // ==================== VALIDATION ====================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
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

    // ==================== RESPONSE BUILDER ====================

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