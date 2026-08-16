package com.tanmay.makemytrip_backend.booking.exception;

public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException(String message) {
        super(message);
    }
}