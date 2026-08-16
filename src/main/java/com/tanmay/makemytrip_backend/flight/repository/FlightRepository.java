package com.tanmay.makemytrip_backend.flight.repository;

import com.tanmay.makemytrip_backend.flight.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    // Used to check flight-number uniqueness.
    boolean existsByFlightNumber(String flightNumber);
    Optional<Flight> findByIdAndActiveTrue(Long id);
}