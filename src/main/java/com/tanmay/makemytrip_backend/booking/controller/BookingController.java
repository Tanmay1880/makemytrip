package com.tanmay.makemytrip_backend.booking.controller;

import com.tanmay.makemytrip_backend.booking.dto.BookingRequest;
import com.tanmay.makemytrip_backend.booking.dto.BookingResponse;
import com.tanmay.makemytrip_backend.booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // ==================== CREATE ====================

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request) {

        return ResponseEntity.ok(
                bookingService.createBooking(request)
        );
    }

    // ==================== READ ====================

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                bookingService.getBookingById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings() {

        return ResponseEntity.ok(
                bookingService.getAllBookings()
        );
    }
}