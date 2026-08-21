package com.tanmay.makemytrip_backend.airport.mapper;

import com.tanmay.makemytrip_backend.airport.dto.AirportRequest;
import com.tanmay.makemytrip_backend.airport.dto.AirportResponse;
import com.tanmay.makemytrip_backend.airport.entity.Airport;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AirportMapper {

    // ==================== REQUEST → ENTITY ====================

    Airport toEntity(AirportRequest request);

    // ==================== ENTITY → RESPONSE ====================

    AirportResponse toResponse(Airport airport);
}