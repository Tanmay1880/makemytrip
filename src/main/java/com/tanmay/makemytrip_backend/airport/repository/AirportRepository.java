package com.tanmay.makemytrip_backend.airport.repository;

import com.tanmay.makemytrip_backend.airport.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirportRepository extends JpaRepository<Airport, Long> {

    // Used to check airport code uniqueness before creation.
    boolean existsByCode(String code);
}