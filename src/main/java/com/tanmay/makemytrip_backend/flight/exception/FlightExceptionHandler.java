package com.tanmay.makemytrip_backend.flight.exception;

import com.tanmay.makemytrip_backend.airline.exception.AirlineNotFoundException;
import com.tanmay.makemytrip_backend.airport.exception.AirportNotFoundException;
import com.tanmay.makemytrip_backend.common.exception.ApiErrorResponse;
import com.tanmay.makemytrip_backend.flight.controller.FlightController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice(assignableTypes = FlightController.class)
public class FlightExceptionHandler {

    // ==================== FLIGHT NOT FOUND ====================

    @ExceptionHandler(FlightNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleFlightNotFound(
            FlightNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    // ==================== FLIGHT ALREADY EXISTS ====================

    @ExceptionHandler(FlightAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleFlightAlreadyExists(
            FlightAlreadyExistsException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    // ==================== INVALID FLIGHT ====================

    @ExceptionHandler(InvalidFlightException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidFlight(
            InvalidFlightException exception) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
    }

    // ==================== AIRLINE NOT FOUND ====================

    @ExceptionHandler(AirlineNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleAirlineNotFound(
            AirlineNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    // ==================== AIRPORT NOT FOUND ====================

    @ExceptionHandler(AirportNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleAirportNotFound(
            AirportNotFoundException exception) {

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