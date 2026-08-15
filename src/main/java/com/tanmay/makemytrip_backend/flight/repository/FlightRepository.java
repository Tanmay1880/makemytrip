package com.tanmay.makemytrip_backend.flight.repository;

import com.tanmay.makemytrip_backend.flight.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    // Used to check flight-number uniqueness.
    boolean existsByFlightNumber(String flightNumber);
}