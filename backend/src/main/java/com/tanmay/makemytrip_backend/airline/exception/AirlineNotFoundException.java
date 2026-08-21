package com.tanmay.makemytrip_backend.airline.exception;

public class AirlineNotFoundException extends RuntimeException {

    public AirlineNotFoundException(String message) {
        super(message);
    }
}