package com.tanmay.makemytrip_backend.flight.service;

import com.tanmay.makemytrip_backend.airline.entity.Airline;
import com.tanmay.makemytrip_backend.airline.exception.AirlineNotFoundException;
import com.tanmay.makemytrip_backend.airline.repository.AirlineRepository;
import com.tanmay.makemytrip_backend.airport.entity.Airport;
import com.tanmay.makemytrip_backend.airport.exception.AirportNotFoundException;
import com.tanmay.makemytrip_backend.airport.repository.AirportRepository;
import com.tanmay.makemytrip_backend.flight.dto.FlightRequest;
import com.tanmay.makemytrip_backend.flight.dto.FlightResponse;
import com.tanmay.makemytrip_backend.flight.dto.FlightUpdateRequest;
import com.tanmay.makemytrip_backend.flight.entity.Flight;
import com.tanmay.makemytrip_backend.flight.exception.FlightNotFoundException;
import com.tanmay.makemytrip_backend.flight.exception.InvalidFlightException;
import com.tanmay.makemytrip_backend.flight.mapper.FlightMapper;
import com.tanmay.makemytrip_backend.flight.repository.FlightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;
    private final AirlineRepository airlineRepository;
    private final AirportRepository airportRepository;

    public FlightService(
            FlightRepository flightRepository,
            FlightMapper flightMapper,
            AirlineRepository airlineRepository,
            AirportRepository airportRepository) {

        this.flightRepository = flightRepository;
        this.flightMapper = flightMapper;
        this.airlineRepository = airlineRepository;
        this.airportRepository = airportRepository;
    }

    // ==================== CREATE ====================

    public FlightResponse createFlight(FlightRequest request) {

        // ==================== FLIGHT NUMBER ====================

        if (flightRepository.existsByFlightNumber(
                request.getFlightNumber())) {

            throw new InvalidFlightException(
                    "Flight already exists with number: "
                            + request.getFlightNumber()
            );
        }

        // ==================== SCHEDULE ====================

        validateSchedule(
                request.getDepartureTime(),
                request.getArrivalTime()
        );

        // ==================== AIRLINE ====================

        Airline airline =
                airlineRepository.findById(request.getAirlineId())
                        .orElseThrow(() ->
                                new AirlineNotFoundException(
                                        "Airline not found with id: "
                                                + request.getAirlineId()
                                )
                        );

        // ==================== DEPARTURE AIRPORT ====================

        Airport departureAirport =
                airportRepository.findById(
                                request.getDepartureAirportId()
                        )
                        .orElseThrow(() ->
                                new AirportNotFoundException(
                                        "Departure airport not found with id: "
                                                + request.getDepartureAirportId()
                                )
                        );

        // ==================== ARRIVAL AIRPORT ====================

        Airport arrivalAirport =
                airportRepository.findById(
                                request.getArrivalAirportId()
                        )
                        .orElseThrow(() ->
                                new AirportNotFoundException(
                                        "Arrival airport not found with id: "
                                                + request.getArrivalAirportId()
                                )
                        );

        // ==================== AIRPORT VALIDATION ====================

        if (departureAirport.getId()
                .equals(arrivalAirport.getId())) {

            throw new InvalidFlightException(
                    "Departure and arrival airports cannot be the same"
            );
        }

        // ==================== CREATE FLIGHT ====================

        Flight flight = new Flight(
                request.getFlightNumber(),
                airline,
                departureAirport,
                arrivalAirport,
                request.getDepartureTime(),
                request.getArrivalTime(),
                request.getEconomySeatsAvailable(),
                request.getPremiumEconomySeatsAvailable(),
                request.getBusinessSeatsAvailable(),
                request.getEconomyPrice(),
                request.getPremiumEconomyPrice(),
                request.getBusinessPrice()
        );

        Flight savedFlight = flightRepository.save(flight);

        return flightMapper.toResponse(savedFlight);
    }

    // ==================== GET BY ID ====================

    public FlightResponse getFlightById(Long id) {

        Flight flight = flightRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new FlightNotFoundException(
                                "Active flight not found with id: " + id
                        )
                );

        return flightMapper.toResponse(flight);
    }

    // ==================== GET ALL ====================

    public List<FlightResponse> getAllFlights() {

        return flightRepository.findByActiveTrue()
                .stream()
                .map(flightMapper::toResponse)
                .toList();
    }

    // ==================== SEARCH ====================

    public List<FlightResponse> searchFlights(
            Long departureAirportId,
            Long arrivalAirportId,
            LocalDate departureDate) {

        // ==================== DATE VALIDATION ====================

        if (departureDate.isBefore(LocalDate.now())) {
            throw new InvalidFlightException(
                    "Departure date cannot be in the past"
            );
        }

        // ==================== AIRPORT VALIDATION ====================

        Airport departureAirport =
                airportRepository.findById(departureAirportId)
                        .orElseThrow(() ->
                                new AirportNotFoundException(
                                        "Departure airport not found with id: "
                                                + departureAirportId
                                )
                        );

        Airport arrivalAirport =
                airportRepository.findById(arrivalAirportId)
                        .orElseThrow(() ->
                                new AirportNotFoundException(
                                        "Arrival airport not found with id: "
                                                + arrivalAirportId
                                )
                        );

        // ==================== ROUTE VALIDATION ====================

        if (departureAirport.getId()
                .equals(arrivalAirport.getId())) {

            throw new InvalidFlightException(
                    "Departure and arrival airports cannot be the same"
            );
        }

        // ==================== DATE RANGE ====================

        LocalDateTime startOfDay =
                departureDate.atStartOfDay();

        LocalDateTime nextDayStart =
                departureDate.plusDays(1).atStartOfDay();

        // ==================== SEARCH ====================

        return flightRepository
                .findByDepartureAirportIdAndArrivalAirportIdAndDepartureTimeGreaterThanEqualAndDepartureTimeLessThanAndActiveTrueOrderByDepartureTimeAsc(
                        departureAirportId,
                        arrivalAirportId,
                        startOfDay,
                        nextDayStart
                )
                .stream()
                .map(flightMapper::toResponse)
                .toList();
    }

    // ==================== UPDATE ====================

    public FlightResponse updateFlight(
            Long id,
            FlightUpdateRequest request) {

        // ==================== FIND FLIGHT ====================

        Flight flight = flightRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new FlightNotFoundException(
                                "Active flight not found with id: " + id
                        )
                );

        // ==================== FLIGHT NUMBER ====================

        if (!flight.getFlightNumber()
                .equals(request.getFlightNumber())
                && flightRepository.existsByFlightNumber(
                request.getFlightNumber())) {

            throw new InvalidFlightException(
                    "Flight already exists with number: "
                            + request.getFlightNumber()
            );
        }

        // ==================== SCHEDULE ====================

        validateSchedule(
                request.getDepartureTime(),
                request.getArrivalTime()
        );

        // ==================== AIRLINE ====================

        Airline airline =
                airlineRepository.findById(request.getAirlineId())
                        .orElseThrow(() ->
                                new AirlineNotFoundException(
                                        "Airline not found with id: "
                                                + request.getAirlineId()
                                )
                        );

        // ==================== DEPARTURE AIRPORT ====================

        Airport departureAirport =
                airportRepository.findById(
                                request.getDepartureAirportId()
                        )
                        .orElseThrow(() ->
                                new AirportNotFoundException(
                                        "Departure airport not found with id: "
                                                + request.getDepartureAirportId()
                                )
                        );

        // ==================== ARRIVAL AIRPORT ====================

        Airport arrivalAirport =
                airportRepository.findById(
                                request.getArrivalAirportId()
                        )
                        .orElseThrow(() ->
                                new AirportNotFoundException(
                                        "Arrival airport not found with id: "
                                                + request.getArrivalAirportId()
                                )
                        );

        // ==================== AIRPORT VALIDATION ====================

        if (departureAirport.getId()
                .equals(arrivalAirport.getId())) {

            throw new InvalidFlightException(
                    "Departure and arrival airports cannot be the same"
            );
        }

        // ==================== UPDATE FLIGHT ====================

        flight.setFlightNumber(request.getFlightNumber());
        flight.setAirline(airline);
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);
        flight.setDepartureTime(request.getDepartureTime());
        flight.setArrivalTime(request.getArrivalTime());

        // ==================== UPDATE PRICES ====================

        flight.setEconomyPrice(request.getEconomyPrice());
        flight.setPremiumEconomyPrice(
                request.getPremiumEconomyPrice()
        );
        flight.setBusinessPrice(request.getBusinessPrice());

        // ==================== IMPORTANT ====================
        //
        // Seat availability is intentionally NOT updated here.
        //
        // Inventory is controlled only through:
        // - reserveSeats()
        // - releaseSeats()
        //
        // =====================================================

        Flight updatedFlight =
                flightRepository.save(flight);

        return flightMapper.toResponse(updatedFlight);
    }

    // ==================== DELETE ====================

    @Transactional
    public void deleteFlight(Long id) {

        Flight flight = flightRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new FlightNotFoundException(
                                "Active flight not found with id: " + id
                        )
                );

        // ==================== SOFT DELETE ====================

        flight.setActive(false);

        flightRepository.save(flight);
    }

    // ==================== RESERVE SEATS ====================

    @Transactional
    public void reserveSeats(
            Long flightId,
            com.tanmay.makemytrip_backend.booking.entity.SeatClass seatClass,
            int passengerCount) {

        if (passengerCount <= 0) {
            throw new InvalidFlightException(
                    "Passenger count must be greater than zero"
            );
        }

        Flight flight = flightRepository.findByIdAndActiveTrue(flightId)
                .orElseThrow(() ->
                        new FlightNotFoundException(
                                "Active flight not found with id: "
                                        + flightId
                        )
                );

        switch (seatClass) {

            case ECONOMY -> {

                if (flight.getEconomySeatsAvailable()
                        < passengerCount) {

                    throw new InvalidFlightException(
                            "Not enough ECONOMY seats available"
                    );
                }

                flight.setEconomySeatsAvailable(
                        flight.getEconomySeatsAvailable()
                                - passengerCount
                );
            }

            case PREMIUM_ECONOMY -> {

                if (flight.getPremiumEconomySeatsAvailable()
                        < passengerCount) {

                    throw new InvalidFlightException(
                            "Not enough PREMIUM_ECONOMY seats available"
                    );
                }

                flight.setPremiumEconomySeatsAvailable(
                        flight.getPremiumEconomySeatsAvailable()
                                - passengerCount
                );
            }

            case BUSINESS -> {

                if (flight.getBusinessSeatsAvailable()
                        < passengerCount) {

                    throw new InvalidFlightException(
                            "Not enough BUSINESS seats available"
                    );
                }

                flight.setBusinessSeatsAvailable(
                        flight.getBusinessSeatsAvailable()
                                - passengerCount
                );
            }

            default -> throw new InvalidFlightException(
                    "Invalid seat class"
            );
        }

        flightRepository.save(flight);
    }

    // ==================== RELEASE SEATS ====================

    @Transactional
    public void releaseSeats(
            Long flightId,
            com.tanmay.makemytrip_backend.booking.entity.SeatClass seatClass,
            int passengerCount) {

        if (passengerCount <= 0) {
            throw new InvalidFlightException(
                    "Passenger count must be greater than zero"
            );
        }

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() ->
                        new FlightNotFoundException(
                                "Flight not found with id: " + flightId
                        )
                );

        switch (seatClass) {

            case ECONOMY -> flight.setEconomySeatsAvailable(
                    flight.getEconomySeatsAvailable()
                            + passengerCount
            );

            case PREMIUM_ECONOMY -> flight.setPremiumEconomySeatsAvailable(
                    flight.getPremiumEconomySeatsAvailable()
                            + passengerCount
            );

            case BUSINESS -> flight.setBusinessSeatsAvailable(
                    flight.getBusinessSeatsAvailable()
                            + passengerCount
            );

            default -> throw new InvalidFlightException(
                    "Invalid seat class"
            );
        }

        flightRepository.save(flight);
    }

    // ==================== SCHEDULE VALIDATION ====================

    private void validateSchedule(
            java.time.LocalDateTime departureTime,
            java.time.LocalDateTime arrivalTime) {

        if (!arrivalTime.isAfter(departureTime)) {

            throw new InvalidFlightException(
                    "Arrival time must be after departure time"
            );
        }
    }
}