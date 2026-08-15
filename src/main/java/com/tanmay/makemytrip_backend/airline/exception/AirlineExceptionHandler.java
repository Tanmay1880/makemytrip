package com.tanmay.makemytrip_backend.airline.exception;

import com.tanmay.makemytrip_backend.airline.controller.AirlineController;
import com.tanmay.makemytrip_backend.common.exception.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice(assignableTypes = AirlineController.class)
public class AirlineExceptionHandler {

    // ==================== AIRLINE NOT FOUND ====================

    @ExceptionHandler(AirlineNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleAirlineNotFound(
            AirlineNotFoundException exception) {

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

    // ==================== AIRLINE ALREADY EXISTS ====================

    @ExceptionHandler(AirlineAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleAirlineAlreadyExists(
            AirlineAlreadyExistsException exception) {

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