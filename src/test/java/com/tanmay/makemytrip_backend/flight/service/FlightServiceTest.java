package com.tanmay.makemytrip_backend.flight.service;

import com.tanmay.makemytrip_backend.airline.repository.AirlineRepository;
import com.tanmay.makemytrip_backend.airport.entity.Airport;
import com.tanmay.makemytrip_backend.airport.exception.AirportNotFoundException;
import com.tanmay.makemytrip_backend.airport.repository.AirportRepository;
import com.tanmay.makemytrip_backend.flight.dto.FlightResponse;
import com.tanmay.makemytrip_backend.flight.entity.Flight;
import com.tanmay.makemytrip_backend.flight.exception.InvalidFlightException;
import com.tanmay.makemytrip_backend.flight.mapper.FlightMapper;
import com.tanmay.makemytrip_backend.flight.repository.FlightRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private FlightMapper flightMapper;

    @Mock
    private AirlineRepository airlineRepository;

    @Mock
    private AirportRepository airportRepository;

    @InjectMocks
    private FlightService flightService;

    // ==================== SEARCH ====================

    @Test
    void searchFlights_returnsMatchingFlights() {

        Long departureAirportId = 1L;
        Long arrivalAirportId = 2L;
        LocalDate departureDate = LocalDate.now().plusDays(5);

        Airport departureAirport = mock(Airport.class);
        Airport arrivalAirport = mock(Airport.class);

        when(departureAirport.getId())
                .thenReturn(departureAirportId);

        when(arrivalAirport.getId())
                .thenReturn(arrivalAirportId);

        when(airportRepository.findById(departureAirportId))
                .thenReturn(Optional.of(departureAirport));

        when(airportRepository.findById(arrivalAirportId))
                .thenReturn(Optional.of(arrivalAirport));

        Flight flight = mock(Flight.class);
        FlightResponse response = mock(FlightResponse.class);

        when(flightRepository
                .findByDepartureAirportIdAndArrivalAirportIdAndDepartureTimeGreaterThanEqualAndDepartureTimeLessThanAndActiveTrueOrderByDepartureTimeAsc(
                        eq(departureAirportId),
                        eq(arrivalAirportId),
                        eq(departureDate.atStartOfDay()),
                        eq(departureDate.plusDays(1).atStartOfDay())
                ))
                .thenReturn(List.of(flight));

        when(flightMapper.toResponse(flight))
                .thenReturn(response);

        List<FlightResponse> result =
                flightService.searchFlights(
                        departureAirportId,
                        arrivalAirportId,
                        departureDate
                );

        assertEquals(1, result.size());
        assertSame(response, result.get(0));

        verify(flightRepository)
                .findByDepartureAirportIdAndArrivalAirportIdAndDepartureTimeGreaterThanEqualAndDepartureTimeLessThanAndActiveTrueOrderByDepartureTimeAsc(
                        departureAirportId,
                        arrivalAirportId,
                        departureDate.atStartOfDay(),
                        departureDate.plusDays(1).atStartOfDay()
                );
    }

    @Test
    void searchFlights_returnsEmptyListWhenNoFlightMatches() {

        Long departureAirportId = 1L;
        Long arrivalAirportId = 2L;
        LocalDate departureDate = LocalDate.now().plusDays(5);

        Airport departureAirport = mock(Airport.class);
        Airport arrivalAirport = mock(Airport.class);

        when(departureAirport.getId())
                .thenReturn(departureAirportId);

        when(arrivalAirport.getId())
                .thenReturn(arrivalAirportId);

        when(airportRepository.findById(departureAirportId))
                .thenReturn(Optional.of(departureAirport));

        when(airportRepository.findById(arrivalAirportId))
                .thenReturn(Optional.of(arrivalAirport));

        when(flightRepository
                .findByDepartureAirportIdAndArrivalAirportIdAndDepartureTimeGreaterThanEqualAndDepartureTimeLessThanAndActiveTrueOrderByDepartureTimeAsc(
                        anyLong(),
                        anyLong(),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class)
                ))
                .thenReturn(List.of());

        List<FlightResponse> result =
                flightService.searchFlights(
                        departureAirportId,
                        arrivalAirportId,
                        departureDate
                );

        assertTrue(result.isEmpty());
    }

    @Test
    void searchFlights_rejectsSameAirport() {

        Long airportId = 1L;
        LocalDate departureDate = LocalDate.now().plusDays(5);

        Airport airport = mock(Airport.class);

        when(airport.getId())
                .thenReturn(airportId);

        when(airportRepository.findById(airportId))
                .thenReturn(Optional.of(airport));

        InvalidFlightException exception =
                assertThrows(
                        InvalidFlightException.class,
                        () -> flightService.searchFlights(
                                airportId,
                                airportId,
                                departureDate
                        )
                );

        assertEquals(
                "Departure and arrival airports cannot be the same",
                exception.getMessage()
        );

        verify(flightRepository, never())
                .findByDepartureAirportIdAndArrivalAirportIdAndDepartureTimeGreaterThanEqualAndDepartureTimeLessThanAndActiveTrueOrderByDepartureTimeAsc(
                        anyLong(),
                        anyLong(),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class)
                );
    }

    @Test
    void searchFlights_rejectsPastDate() {

        Long departureAirportId = 1L;
        Long arrivalAirportId = 2L;

        LocalDate pastDate =
                LocalDate.now().minusDays(1);

        InvalidFlightException exception =
                assertThrows(
                        InvalidFlightException.class,
                        () -> flightService.searchFlights(
                                departureAirportId,
                                arrivalAirportId,
                                pastDate
                        )
                );

        assertEquals(
                "Departure date cannot be in the past",
                exception.getMessage()
        );

        verifyNoInteractions(airportRepository);
        verifyNoInteractions(flightRepository);
    }

    @Test
    void searchFlights_rejectsUnknownDepartureAirport() {

        Long departureAirportId = 99L;
        Long arrivalAirportId = 2L;
        LocalDate departureDate = LocalDate.now().plusDays(5);

        when(airportRepository.findById(departureAirportId))
                .thenReturn(Optional.empty());

        assertThrows(
                AirportNotFoundException.class,
                () -> flightService.searchFlights(
                        departureAirportId,
                        arrivalAirportId,
                        departureDate
                )
        );

        verify(airportRepository)
                .findById(departureAirportId);

        verifyNoMoreInteractions(airportRepository);
        verifyNoInteractions(flightRepository);
    }

    @Test
    void searchFlights_rejectsUnknownArrivalAirport() {

        Long departureAirportId = 1L;
        Long arrivalAirportId = 99L;
        LocalDate departureDate = LocalDate.now().plusDays(5);

        Airport departureAirport = mock(Airport.class);

        when(airportRepository.findById(departureAirportId))
                .thenReturn(Optional.of(departureAirport));

        when(airportRepository.findById(arrivalAirportId))
                .thenReturn(Optional.empty());

        assertThrows(
                AirportNotFoundException.class,
                () -> flightService.searchFlights(
                        departureAirportId,
                        arrivalAirportId,
                        departureDate
                )
        );

        verify(airportRepository)
                .findById(departureAirportId);

        verify(airportRepository)
                .findById(arrivalAirportId);

        verifyNoInteractions(flightRepository);
    }
}