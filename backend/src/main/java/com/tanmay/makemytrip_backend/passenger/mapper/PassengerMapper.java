package com.tanmay.makemytrip_backend.passenger.mapper;

import com.tanmay.makemytrip_backend.passenger.dto.PassengerResponse;
import com.tanmay.makemytrip_backend.passenger.entity.Passenger;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PassengerMapper {

    @Mapping(source = "booking.id", target = "bookingId")
    PassengerResponse toResponse(Passenger passenger);
}