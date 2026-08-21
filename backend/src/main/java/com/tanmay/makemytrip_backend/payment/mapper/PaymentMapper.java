package com.tanmay.makemytrip_backend.payment.mapper;

import com.tanmay.makemytrip_backend.payment.dto.PaymentResponse;
import com.tanmay.makemytrip_backend.payment.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "booking.id", target = "bookingId")
    PaymentResponse toResponse(Payment payment);
}