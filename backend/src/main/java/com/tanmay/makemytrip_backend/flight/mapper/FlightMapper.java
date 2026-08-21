package com.tanmay.makemytrip_backend.flight.mapper;

import com.tanmay.makemytrip_backend.flight.dto.FlightResponse;
import com.tanmay.makemytrip_backend.flight.entity.Flight;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FlightMapper {

    // ==================== ENTITY → RESPONSE ====================

    @Mapping(source = "airline.id", target = "airlineId")
    @Mapping(source = "airline.name", target = "airlineName")
    @Mapping(source = "airline.code", target = "airlineCode")

    @Mapping(source = "departureAirport.id", target = "departureAirportId")
    @Mapping(source = "departureAirport.name", target = "departureAirportName")
    @Mapping(source = "departureAirport.code", target = "departureAirportCode")

    @Mapping(source = "arrivalAirport.id", target = "arrivalAirportId")
    @Mapping(source = "arrivalAirport.name", target = "arrivalAirportName")
    @Mapping(source = "arrivalAirport.code", target = "arrivalAirportCode")

    FlightResponse toResponse(Flight flight);
}