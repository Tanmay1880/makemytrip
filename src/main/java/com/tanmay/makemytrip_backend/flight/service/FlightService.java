package com.tanmay.makemytrip_backend.flight.service;

import com.tanmay.makemytrip_backend.airline.entity.Airline;
import com.tanmay.makemytrip_backend.airline.exception.AirlineNotFoundException;
import com.tanmay.makemytrip_backend.airline.repository.AirlineRepository;
import com.tanmay.makemytrip_backend.airport.entity.Airport;
import com.tanmay.makemytrip_backend.airport.exception.AirportNotFoundException;
import com.tanmay.makemytrip_backend.airport.repository.AirportRepository;
import com.tanmay.makemytrip_backend.booking.entity.SeatClass;
import com.tanmay.makemytrip_backend.flight.dto.FlightRequest;
import com.tanmay.makemytrip_backend.flight.dto.FlightResponse;
import com.tanmay.makemytrip_backend.flight.entity.Flight;
import com.tanmay.makemytrip_backend.flight.exception.FlightAlreadyExistsException;
import com.tanmay.makemytrip_backend.flight.exception.FlightNotFoundException;
import com.tanmay.makemytrip_backend.flight.exception.InvalidFlightException;
import com.tanmay.makemytrip_backend.flight.mapper.FlightMapper;
import com.tanmay.makemytrip_backend.flight.repository.FlightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final AirlineRepository airlineRepository;
    private final AirportRepository airportRepository;
    private final FlightMapper flightMapper;

    public FlightService(
            FlightRepository flightRepository,
            AirlineRepository airlineRepository,
            AirportRepository airportRepository,
            FlightMapper flightMapper) {

        this.flightRepository = flightRepository;
        this.airlineRepository = airlineRepository;
        this.airportRepository = airportRepository;
        this.flightMapper = flightMapper;
    }

    // ==================== CREATE ====================

    public FlightResponse createFlight(FlightRequest request) {

        validateFlightSchedule(request);

        Airline airline = airlineRepository.findById(request.getAirlineId())
                .orElseThrow(() ->
                        new AirlineNotFoundException(
                                "Airline not found with id: "
                                        + request.getAirlineId()
                        )
                );

        Airport departureAirport =
                airportRepository.findById(request.getDepartureAirportId())
                        .orElseThrow(() ->
                                new AirportNotFoundException(
                                        "Airport not found with id: "
                                                + request.getDepartureAirportId()
                                )
                        );

        Airport arrivalAirport =
                airportRepository.findById(request.getArrivalAirportId())
                        .orElseThrow(() ->
                                new AirportNotFoundException(
                                        "Airport not found with id: "
                                                + request.getArrivalAirportId()
                                )
                        );

        if (flightRepository.existsByFlightNumber(
                request.getFlightNumber())) {

            throw new FlightAlreadyExistsException(
                    "Flight with this flight number already exists"
            );
        }

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

        // New flights are active by default.
        flight.setActive(true);

        Flight savedFlight = flightRepository.save(flight);

        return flightMapper.toResponse(savedFlight);
    }

    // ==================== READ ====================

    public FlightResponse getFlightById(Long id) {

        Flight flight = flightRepository.findById(id)
                .orElseThrow(() ->
                        new FlightNotFoundException(
                                "Flight not found with id: " + id
                        )
                );

        return flightMapper.toResponse(flight);
    }

    public List<FlightResponse> getAllFlights() {

        List<Flight> flights = flightRepository.findAll();

        return flights.stream()
                .map(flightMapper::toResponse)
                .toList();
    }

    // ==================== UPDATE ====================

    public FlightResponse updateFlight(
            Long id,
            FlightRequest request) {

        validateFlightSchedule(request);

        Flight flight = flightRepository.findById(id)
                .orElseThrow(() ->
                        new FlightNotFoundException(
                                "Flight not found with id: " + id
                        )
                );

        Airline airline = airlineRepository.findById(request.getAirlineId())
                .orElseThrow(() ->
                        new AirlineNotFoundException(
                                "Airline not found with id: "
                                        + request.getAirlineId()
                        )
                );

        Airport departureAirport =
                airportRepository.findById(request.getDepartureAirportId())
                        .orElseThrow(() ->
                                new AirportNotFoundException(
                                        "Airport not found with id: "
                                                + request.getDepartureAirportId()
                                )
                        );

        Airport arrivalAirport =
                airportRepository.findById(request.getArrivalAirportId())
                        .orElseThrow(() ->
                                new AirportNotFoundException(
                                        "Airport not found with id: "
                                                + request.getArrivalAirportId()
                                )
                        );

        if (!flight.getFlightNumber().equals(request.getFlightNumber())
                && flightRepository.existsByFlightNumber(
                request.getFlightNumber())) {

            throw new FlightAlreadyExistsException(
                    "Flight with this flight number already exists"
            );
        }

        flight.setFlightNumber(request.getFlightNumber());
        flight.setAirline(airline);
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);
        flight.setDepartureTime(request.getDepartureTime());
        flight.setArrivalTime(request.getArrivalTime());
        flight.setEconomySeatsAvailable(
                request.getEconomySeatsAvailable()
        );
        flight.setPremiumEconomySeatsAvailable(
                request.getPremiumEconomySeatsAvailable()
        );
        flight.setBusinessSeatsAvailable(
                request.getBusinessSeatsAvailable()
        );
        flight.setEconomyPrice(request.getEconomyPrice());
        flight.setPremiumEconomyPrice(request.getPremiumEconomyPrice());
        flight.setBusinessPrice(request.getBusinessPrice());

        Flight updatedFlight = flightRepository.save(flight);

        return flightMapper.toResponse(updatedFlight);
    }

    // ==================== DELETE ====================

    public void deleteFlight(Long id) {

        Flight flight = flightRepository.findById(id)
                .orElseThrow(() ->
                        new FlightNotFoundException(
                                "Flight not found with id: " + id
                        )
                );

        // Soft delete: preserve flights referenced by bookings.
        flight.setActive(false);

        flightRepository.save(flight);
    }

    // ==================== RESERVE SEATS ====================

    @Transactional
    public void reserveSeats(
            Long flightId,
            SeatClass seatClass,
            int passengerCount) {

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() ->
                        new FlightNotFoundException(
                                "Flight not found with id: " + flightId
                        )
                );

        if (!Boolean.TRUE.equals(flight.getActive())) {
            throw new InvalidFlightException(
                    "Cannot reserve seats on an inactive flight"
            );
        }

        if (passengerCount <= 0) {
            throw new InvalidFlightException(
                    "Passenger count must be greater than 0"
            );
        }

        switch (seatClass) {

            case ECONOMY -> {

                if (flight.getEconomySeatsAvailable() < passengerCount) {
                    throw new InvalidFlightException(
                            "Not enough economy seats available"
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
                            "Not enough premium economy seats available"
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
                            "Not enough business seats available"
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
            SeatClass seatClass,
            int passengerCount) {

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() ->
                        new FlightNotFoundException(
                                "Flight not found with id: " + flightId
                        )
                );

        if (passengerCount <= 0) {
            throw new InvalidFlightException(
                    "Passenger count must be greater than 0"
            );
        }

        switch (seatClass) {

            case ECONOMY -> flight.setEconomySeatsAvailable(
                    flight.getEconomySeatsAvailable() + passengerCount
            );

            case PREMIUM_ECONOMY -> flight.setPremiumEconomySeatsAvailable(
                    flight.getPremiumEconomySeatsAvailable() + passengerCount
            );

            case BUSINESS -> flight.setBusinessSeatsAvailable(
                    flight.getBusinessSeatsAvailable() + passengerCount
            );

            default -> throw new InvalidFlightException(
                    "Invalid seat class"
            );
        }

        flightRepository.save(flight);
    }

    // ==================== VALIDATION ====================

    private void validateFlightSchedule(FlightRequest request) {

        if (request.getDepartureAirportId()
                .equals(request.getArrivalAirportId())) {

            throw new InvalidFlightException(
                    "Departure and arrival airports cannot be the same"
            );
        }

        if (!request.getArrivalTime()
                .isAfter(request.getDepartureTime())) {

            throw new InvalidFlightException(
                    "Arrival time must be after departure time"
            );
        }
    }
}