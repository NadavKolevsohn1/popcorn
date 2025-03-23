package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.model.Ticket;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repo.TicketRepo;
import com.att.tdp.popcorn_palace.repo.ShowtimeRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final TicketRepo ticketRepository;
    private final ShowtimeRepo showtimeRepository;

    public BookingController(TicketRepo ticketRepository, ShowtimeRepo showtimeRepository) {
        this.ticketRepository = ticketRepository;
        this.showtimeRepository = showtimeRepository;
    }

    // Booking a ticket
    @PostMapping
    public ResponseEntity<?> bookTicket(@RequestBody Map<String, Object> request) {
        // Checking that the required fields are in the request payload
        if (!request.containsKey("showtimeId") || !request.containsKey("seatNumber")
                || !request.containsKey("userId")) {
            return ResponseEntity.badRequest().body("Missing required fields: showtimeId, seatNumber, or userId");
        }

        // Parse and validate by order: showtimeId, seatNumber
        Long showtimeId;
        try {
            showtimeId = Long.parseLong(request.get("showtimeId").toString());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid showtimeId");
        }

        Integer seatNumber;
        try {
            seatNumber = Integer.parseInt(request.get("seatNumber").toString());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid seatNumber");
        }

        String userId = request.get("userId").toString();

        // Retrieve showtime by id
        Optional<Showtime> optional_Showtime = showtimeRepository.findById(showtimeId);
        if (!optional_Showtime.isPresent()) {
            return ResponseEntity.badRequest().body("Showtime not found with id: " + showtimeId);
        }
        Showtime showtime = optional_Showtime.get();

        // Ensuring seat is not already booked
        if (ticketRepository.existsByShowtimeAndSeatNumber(showtime, seatNumber)) {
            return ResponseEntity.badRequest().body("Seat " + seatNumber + " is already booked");
        }
        // Creating and adding the ticket
        Ticket ticket = new Ticket();
        ticket.setShowtime(showtime);
        ticket.setSeatNumber(seatNumber);
        ticket.setUserId(userId);

        Ticket savedTicket = ticketRepository.save(ticket);

        // Return the booking Id
        return ResponseEntity.ok(Map.of("bookingId", savedTicket.getBookingId().toString()));
    }

}
