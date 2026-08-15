package com.tanmay.makemytrip_backend.airport.controller;

import com.tanmay.makemytrip_backend.airport.dto.AirportRequest;
import com.tanmay.makemytrip_backend.airport.dto.AirportResponse;
import com.tanmay.makemytrip_backend.airport.service.AirportService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/airports")
public class AirportController {

    private final AirportService airportService;

    public AirportController(AirportService airportService) {
        this.airportService = airportService;
    }

    // ==================== CREATE ====================

    @PostMapping
    public ResponseEntity<AirportResponse> createAirport(
            @Valid @RequestBody AirportRequest request) {

        return ResponseEntity.ok(
                airportService.createAirport(request)
        );
    }

    // ==================== READ ====================

    @GetMapping("/{id}")
    public ResponseEntity<AirportResponse> getAirportById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                airportService.getAirportById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<AirportResponse>> getAllAirports() {

        return ResponseEntity.ok(
                airportService.getAllAirports()
        );
    }

    // ==================== UPDATE ====================

    @PutMapping("/{id}")
    public ResponseEntity<AirportResponse> updateAirport(
            @PathVariable Long id,
            @Valid @RequestBody AirportRequest request) {

        return ResponseEntity.ok(
                airportService.updateAirport(id, request)
        );
    }

    // ==================== DELETE ====================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAirport(
            @PathVariable Long id) {

        airportService.deleteAirport(id);

        return ResponseEntity.noContent().build();
    }
}