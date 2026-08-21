package com.tanmay.makemytrip_backend.booking.repository;

import com.tanmay.makemytrip_backend.booking.entity.Booking;
import com.tanmay.makemytrip_backend.booking.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByStatusAndExpiresAtBefore(
            BookingStatus status,
            LocalDateTime now
    );

    List<Booking> findByUser_Id(Long userId);
}