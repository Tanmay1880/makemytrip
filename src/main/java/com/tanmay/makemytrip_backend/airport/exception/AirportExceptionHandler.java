package com.tanmay.makemytrip_backend.airport.exception;

import com.tanmay.makemytrip_backend.airport.controller.AirportController;
import com.tanmay.makemytrip_backend.common.exception.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice(assignableTypes = AirportController.class)
public class AirportExceptionHandler {

    // ==================== AIRPORT NOT FOUND ====================

    @ExceptionHandler(AirportNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleAirportNotFound(
            AirportNotFoundException exception) {

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                LocalDateTime.now(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    // ==================== AIRPORT ALREADY EXISTS ====================

    @ExceptionHandler(AirportAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleAirportAlreadyExists(
            AirportAlreadyExistsException exception) {

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                exception.getMessage(),
                LocalDateTime.now(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }
}