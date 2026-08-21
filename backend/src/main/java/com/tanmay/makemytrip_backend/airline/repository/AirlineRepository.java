package com.tanmay.makemytrip_backend.airline.repository;

import com.tanmay.makemytrip_backend.airline.entity.Airline;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirlineRepository extends JpaRepository<Airline, Long> {

    // Used to check airline code uniqueness before creation.
    boolean existsByCode(String code);
}