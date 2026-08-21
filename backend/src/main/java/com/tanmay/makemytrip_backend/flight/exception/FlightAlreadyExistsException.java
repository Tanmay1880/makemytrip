package com.tanmay.makemytrip_backend.flight.exception;

public class FlightAlreadyExistsException extends RuntimeException {

    public FlightAlreadyExistsException(String message) {
        super(message);
    }
}