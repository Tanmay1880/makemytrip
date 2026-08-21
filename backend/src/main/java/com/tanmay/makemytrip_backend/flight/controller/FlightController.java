package com.tanmay.makemytrip_backend.flight.controller;

import com.tanmay.makemytrip_backend.flight.dto.FlightRequest;
import com.tanmay.makemytrip_backend.flight.dto.FlightResponse;
import com.tanmay.makemytrip_backend.flight.dto.FlightUpdateRequest;
import com.tanmay.makemytrip_backend.flight.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    // ==================== CREATE ====================

    @PostMapping
    public ResponseEntity<FlightResponse> createFlight(
            @Valid @RequestBody FlightRequest request) {

        return ResponseEntity.ok(
                flightService.createFlight(request)
        );
    }

    // ==================== READ ====================

    @GetMapping("/{id}")
    public ResponseEntity<FlightResponse> getFlightById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                flightService.getFlightById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<FlightResponse>> getAllFlights() {

        return ResponseEntity.ok(
                flightService.getAllFlights()
        );
    }

    // ==================== UPDATE ====================

    @PutMapping("/{id}")
    public ResponseEntity<FlightResponse> updateFlight(
            @PathVariable Long id,
            @Valid @RequestBody FlightUpdateRequest request) {

        return ResponseEntity.ok(
                flightService.updateFlight(id, request)
        );
    }

    // ==================== DELETE ====================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(
            @PathVariable Long id) {

        flightService.deleteFlight(id);

        return ResponseEntity.noContent().build();
    }

    // ==================== SEARCH ====================

    @GetMapping("/search")
    public ResponseEntity<List<FlightResponse>> searchFlights(
            @RequestParam Long departureAirportId,
            @RequestParam Long arrivalAirportId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate departureDate) {

        return ResponseEntity.ok(
                flightService.searchFlights(
                        departureAirportId,
                        arrivalAirportId,
                        departureDate
                )
        );
    }
}