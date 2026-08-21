package com.tanmay.makemytrip_backend.flight.exception;

public class InvalidFlightException extends RuntimeException {

    public InvalidFlightException(String message) {
        super(message);
    }
}