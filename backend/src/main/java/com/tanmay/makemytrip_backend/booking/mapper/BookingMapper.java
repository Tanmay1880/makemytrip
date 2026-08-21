package com.tanmay.makemytrip_backend.booking.mapper;

import com.tanmay.makemytrip_backend.booking.dto.BookingResponse;
import com.tanmay.makemytrip_backend.booking.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    // ==================== ENTITY → RESPONSE ====================

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "flight.id", target = "flightId")
    @Mapping(source = "flight.flightNumber", target = "flightNumber")
    BookingResponse toResponse(Booking booking);
}