package com.tanmay.makemytrip_backend.flight.repository;

import com.tanmay.makemytrip_backend.flight.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    // Used to check flight-number uniqueness.
    boolean existsByFlightNumber(String flightNumber);

    Optional<Flight> findByIdAndActiveTrue(Long id);

    List<Flight> findByActiveTrue();

    // ==================== SEARCH ====================

    List<Flight>
    findByDepartureAirportIdAndArrivalAirportIdAndDepartureTimeGreaterThanEqualAndDepartureTimeLessThanAndActiveTrueOrderByDepartureTimeAsc(
            Long departureAirportId,
            Long arrivalAirportId,
            LocalDateTime startOfDay,
            LocalDateTime nextDayStart
    );
}