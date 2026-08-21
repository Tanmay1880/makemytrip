package com.tanmay.makemytrip_backend.airline.controller;

import com.tanmay.makemytrip_backend.airline.dto.AirlineRequest;
import com.tanmay.makemytrip_backend.airline.dto.AirlineResponse;
import com.tanmay.makemytrip_backend.airline.service.AirlineService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/airlines")
public class AirlineController {

    private final AirlineService airlineService;

    public AirlineController(AirlineService airlineService) {
        this.airlineService = airlineService;
    }

    // ==================== CREATE ====================

    @PostMapping
    public ResponseEntity<AirlineResponse> createAirline(
            @Valid @RequestBody AirlineRequest request) {

        return ResponseEntity.ok(
                airlineService.createAirline(request)
        );
    }

    // ==================== READ ====================

    @GetMapping("/{id}")
    public ResponseEntity<AirlineResponse> getAirlineById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                airlineService.getAirlineById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<AirlineResponse>> getAllAirlines() {

        return ResponseEntity.ok(
                airlineService.getAllAirlines()
        );
    }

    // ==================== UPDATE ====================

    @PutMapping("/{id}")
    public ResponseEntity<AirlineResponse> updateAirline(
            @PathVariable Long id,
            @Valid @RequestBody AirlineRequest request) {

        return ResponseEntity.ok(
                airlineService.updateAirline(id, request)
        );
    }

    // ==================== DELETE ====================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAirline(
            @PathVariable Long id) {

        airlineService.deleteAirline(id);

        return ResponseEntity.noContent().build();
    }
}