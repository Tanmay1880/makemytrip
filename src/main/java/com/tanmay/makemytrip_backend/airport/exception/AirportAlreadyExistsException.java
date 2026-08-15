package com.tanmay.makemytrip_backend.airport.exception;

public class AirportAlreadyExistsException extends RuntimeException {

    public AirportAlreadyExistsException(String message) {
        super(message);
    }
}