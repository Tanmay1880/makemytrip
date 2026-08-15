package com.tanmay.makemytrip_backend.airport.service;

import com.tanmay.makemytrip_backend.airport.dto.AirportRequest;
import com.tanmay.makemytrip_backend.airport.dto.AirportResponse;
import com.tanmay.makemytrip_backend.airport.entity.Airport;
import com.tanmay.makemytrip_backend.airport.exception.AirportAlreadyExistsException;
import com.tanmay.makemytrip_backend.airport.exception.AirportNotFoundException;
import com.tanmay.makemytrip_backend.airport.mapper.AirportMapper;
import com.tanmay.makemytrip_backend.airport.repository.AirportRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AirportService {

    private final AirportRepository airportRepository;
    private final AirportMapper airportMapper;

    public AirportService(
            AirportRepository airportRepository,
            AirportMapper airportMapper) {

        this.airportRepository = airportRepository;
        this.airportMapper = airportMapper;
    }

    // ==================== CREATE ====================

    public AirportResponse createAirport(AirportRequest request) {

        if (airportRepository.existsByCode(request.getCode())) {
            throw new AirportAlreadyExistsException(
                    "Airport with this code already exists"
            );
        }

        Airport airport = airportMapper.toEntity(request);

        // New airports are active by default.
        airport.setActive(true);

        Airport savedAirport = airportRepository.save(airport);

        return airportMapper.toResponse(savedAirport);
    }

    // ==================== READ ====================

    public AirportResponse getAirportById(Long id) {

        Airport airport = airportRepository.findById(id)
                .orElseThrow(() ->
                        new AirportNotFoundException(
                                "Airport not found with id: " + id
                        )
                );

        return airportMapper.toResponse(airport);
    }

    public List<AirportResponse> getAllAirports() {

        List<Airport> airports = airportRepository.findAll();

        return airports.stream()
                .map(airportMapper::toResponse)
                .toList();
    }

    // ==================== UPDATE ====================

    public AirportResponse updateAirport(
            Long id,
            AirportRequest request) {

        Airport airport = airportRepository.findById(id)
                .orElseThrow(() ->
                        new AirportNotFoundException(
                                "Airport not found with id: " + id
                        )
                );

        /*
         * Airport code is unique.
         * If the code is changed, make sure the new code
         * does not already belong to another airport.
         */
        if (!airport.getCode().equals(request.getCode())
                && airportRepository.existsByCode(request.getCode())) {

            throw new AirportAlreadyExistsException(
                    "Airport with this code already exists"
            );
        }

        airport.setName(request.getName());
        airport.setCode(request.getCode());
        airport.setCity(request.getCity());
        airport.setCountry(request.getCountry());

        Airport updatedAirport = airportRepository.save(airport);

        return airportMapper.toResponse(updatedAirport);
    }

    // ==================== DELETE ====================

    public void deleteAirport(Long id) {

        Airport airport = airportRepository.findById(id)
                .orElseThrow(() ->
                        new AirportNotFoundException(
                                "Airport not found with id: " + id
                        )
                );

        // Soft delete: preserve airports referenced by flights.
        airport.setActive(false);

        airportRepository.save(airport);
    }
}