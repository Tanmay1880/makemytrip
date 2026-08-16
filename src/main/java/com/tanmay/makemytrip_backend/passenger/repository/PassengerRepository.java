package com.tanmay.makemytrip_backend.passenger.repository;

import com.tanmay.makemytrip_backend.passenger.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {

    long countByBookingId(Long bookingId);

    List<Passenger> findByBookingId(Long bookingId);
}