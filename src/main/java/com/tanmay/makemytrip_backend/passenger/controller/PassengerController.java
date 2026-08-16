package com.tanmay.makemytrip_backend.passenger.controller;

import com.tanmay.makemytrip_backend.passenger.dto.PassengerRequest;
import com.tanmay.makemytrip_backend.passenger.dto.PassengerResponse;
import com.tanmay.makemytrip_backend.passenger.service.PassengerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PassengerController {

    private final PassengerService passengerService;

    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    // ==================== CREATE ====================

    @PostMapping("/bookings/{bookingId}/passengers")
    public ResponseEntity<PassengerResponse> createPassenger(
            @PathVariable Long bookingId,
            @Valid @RequestBody PassengerRequest request) {

        return ResponseEntity.ok(
                passengerService.createPassenger(bookingId, request)
        );
    }

    // ==================== GET BY ID ====================

    @GetMapping("/passengers/{id}")
    public ResponseEntity<PassengerResponse> getPassengerById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                passengerService.getPassengerById(id)
        );
    }

    // ==================== GET BY BOOKING ====================

    @GetMapping("/bookings/{bookingId}/passengers")
    public ResponseEntity<List<PassengerResponse>> getPassengersByBookingId(
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                passengerService.getPassengersByBookingId(bookingId)
        );
    }
}