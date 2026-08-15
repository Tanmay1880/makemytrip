package com.tanmay.makemytrip_backend.airline.service;

import com.tanmay.makemytrip_backend.airline.dto.AirlineRequest;
import com.tanmay.makemytrip_backend.airline.dto.AirlineResponse;
import com.tanmay.makemytrip_backend.airline.entity.Airline;
import com.tanmay.makemytrip_backend.airline.exception.AirlineAlreadyExistsException;
import com.tanmay.makemytrip_backend.airline.exception.AirlineNotFoundException;
import com.tanmay.makemytrip_backend.airline.mapper.AirlineMapper;
import com.tanmay.makemytrip_backend.airline.repository.AirlineRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AirlineService {

    private final AirlineRepository airlineRepository;
    private final AirlineMapper airlineMapper;

    public AirlineService(
            AirlineRepository airlineRepository,
            AirlineMapper airlineMapper) {

        this.airlineRepository = airlineRepository;
        this.airlineMapper = airlineMapper;
    }

    // ==================== CREATE ====================

    public AirlineResponse createAirline(AirlineRequest request) {

        if (airlineRepository.existsByCode(request.getCode())) {
            throw new AirlineAlreadyExistsException(
                    "Airline with this code already exists"
            );
        }

        Airline airline = airlineMapper.toEntity(request);

        // New airlines are active by default.
        airline.setActive(true);

        Airline savedAirline = airlineRepository.save(airline);

        return airlineMapper.toResponse(savedAirline);
    }

    // ==================== READ ====================

    public AirlineResponse getAirlineById(Long id) {

        Airline airline = airlineRepository.findById(id)
                .orElseThrow(() ->
                        new AirlineNotFoundException(
                                "Airline not found with id: " + id
                        )
                );

        return airlineMapper.toResponse(airline);
    }

    public List<AirlineResponse> getAllAirlines() {

        List<Airline> airlines = airlineRepository.findAll();

        return airlines.stream()
                .map(airlineMapper::toResponse)
                .toList();
    }

    // ==================== UPDATE ====================

    public AirlineResponse updateAirline(
            Long id,
            AirlineRequest request) {

        Airline airline = airlineRepository.findById(id)
                .orElseThrow(() ->
                        new AirlineNotFoundException(
                                "Airline not found with id: " + id
                        )
                );

        /*
         * The airline code is unique.
         * If the client changes it, make sure the new code
         * does not already belong to another airline.
         */
        if (!airline.getCode().equals(request.getCode())
                && airlineRepository.existsByCode(request.getCode())) {

            throw new AirlineAlreadyExistsException(
                    "Airline with this code already exists"
            );
        }

        airline.setName(request.getName());
        airline.setCode(request.getCode());

        Airline updatedAirline = airlineRepository.save(airline);

        return airlineMapper.toResponse(updatedAirline);
    }

    // ==================== DELETE ====================

    public void deleteAirline(Long id) {

        Airline airline = airlineRepository.findById(id)
                .orElseThrow(() ->
                        new AirlineNotFoundException(
                                "Airline not found with id: " + id
                        )
                );

        // Soft delete: preserve airlines referenced by historical flights.
        airline.setActive(false);

        airlineRepository.save(airline);
    }
}