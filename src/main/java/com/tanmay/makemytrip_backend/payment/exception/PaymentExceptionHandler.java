package com.tanmay.makemytrip_backend.payment.exception;

import com.tanmay.makemytrip_backend.booking.exception.BookingNotFoundException;
import com.tanmay.makemytrip_backend.common.exception.ApiErrorResponse;
import com.tanmay.makemytrip_backend.payment.controller.PaymentController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice(assignableTypes = PaymentController.class)
public class PaymentExceptionHandler {

    // ==================== BOOKING NOT FOUND ====================

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleBookingNotFound(
            BookingNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    // ==================== INVALID PAYMENT ====================

    @ExceptionHandler(InvalidPaymentException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidPayment(
            InvalidPaymentException exception) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
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