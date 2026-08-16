package com.tanmay.makemytrip_backend.passenger.exception;

public class PassengerNotFoundException extends RuntimeException {

    public PassengerNotFoundException(String message) {
        super(message);
    }
}