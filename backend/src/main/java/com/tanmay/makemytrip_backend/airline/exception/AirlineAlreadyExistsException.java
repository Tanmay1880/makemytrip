package com.tanmay.makemytrip_backend.airline.exception;

public class AirlineAlreadyExistsException extends RuntimeException {

    public AirlineAlreadyExistsException(String message) {
        super(message);
    }
}