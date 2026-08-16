package com.tanmay.makemytrip_backend.booking.repository;

import com.tanmay.makemytrip_backend.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {

}