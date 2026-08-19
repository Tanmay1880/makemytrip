package com.tanmay.makemytrip_backend.flight.service;

import com.tanmay.makemytrip_backend.airline.entity.Airline;
import com.tanmay.makemytrip_backend.airline.repository.AirlineRepository;
import com.tanmay.makemytrip_backend.airport.entity.Airport;
import com.tanmay.makemytrip_backend.airport.repository.AirportRepository;
import com.tanmay.makemytrip_backend.booking.entity.SeatClass;
import com.tanmay.makemytrip_backend.flight.entity.Flight;
import com.tanmay.makemytrip_backend.flight.repository.FlightRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FlightServiceConcurrencyTest {

    @Autowired
    private FlightService flightService;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private AirlineRepository airlineRepository;

    @Autowired
    private AirportRepository airportRepository;

    @Test
    void reserveSeats_concurrently_shouldNotOversell() throws Exception {

        // ==================== TEST DATA ====================

        Airline airline =
                airlineRepository.findById(1L)
                        .orElseThrow();

        var departureAirport =
                airportRepository.findById(1L)
                        .orElseThrow();

        var arrivalAirport =
                airportRepository.findById(2L)
                        .orElseThrow();

        Flight flight = new Flight(
                "CONC-" + System.currentTimeMillis(),
                airline,
                departureAirport,
                arrivalAirport,
                LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(10).plusHours(2),
                1,      // ONE ECONOMY SEAT
                0,
                0,
                new BigDecimal("500.00"),
                new BigDecimal("800.00"),
                new BigDecimal("1500.00")
        );

        flight.setActive(true);

        Flight savedFlight =
                flightRepository.saveAndFlush(flight);

        Long flightId = savedFlight.getId();

        // ==================== CONCURRENT RESERVATIONS ====================

        ExecutorService executor =
                Executors.newFixedThreadPool(2);

        CountDownLatch startLatch =
                new CountDownLatch(1);

        Callable<Boolean> reservation = () -> {

            startLatch.await();

            try {
                flightService.reserveSeats(
                        flightId,
                        SeatClass.ECONOMY,
                        1
                );

                return true;

            } catch (Exception exception) {

                return false;
            }
        };

        Future<Boolean> first =
                executor.submit(reservation);

        Future<Boolean> second =
                executor.submit(reservation);

        // Release both threads at approximately the same time.
        startLatch.countDown();

        boolean firstResult =
                first.get(10, TimeUnit.SECONDS);

        boolean secondResult =
                second.get(10, TimeUnit.SECONDS);

        executor.shutdown();

        // ==================== VERIFY ====================

        long successfulReservations =
                List.of(firstResult, secondResult)
                        .stream()
                        .filter(Boolean::booleanValue)
                        .count();

        assertEquals(
                1,
                successfulReservations,
                "Exactly one reservation should succeed"
        );

        Flight finalFlight =
                flightRepository.findById(flightId)
                        .orElseThrow();

        assertEquals(
                0,
                finalFlight.getEconomySeatsAvailable(),
                "The flight must never be oversold"
        );

        assertTrue(
                finalFlight.getEconomySeatsAvailable() >= 0,
                "Seat inventory must never become negative"
        );

        // ==================== CLEANUP ====================

        flightRepository.deleteById(flightId);
    }
}