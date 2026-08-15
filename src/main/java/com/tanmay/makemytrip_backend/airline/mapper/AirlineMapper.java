package com.tanmay.makemytrip_backend.airline.mapper;

import com.tanmay.makemytrip_backend.airline.dto.AirlineRequest;
import com.tanmay.makemytrip_backend.airline.dto.AirlineResponse;
import com.tanmay.makemytrip_backend.airline.entity.Airline;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AirlineMapper {

    // ==================== REQUEST → ENTITY ====================

    /**
     * Converts an airline creation request into an Airline entity.
     */
    Airline toEntity(AirlineRequest request);

    // ==================== ENTITY → RESPONSE ====================

    /**
     * Converts an Airline entity into an API response.
     */
    AirlineResponse toResponse(Airline airline);
}