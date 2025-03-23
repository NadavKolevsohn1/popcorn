package com.att.tdp.popcorn_palace.repo;

import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.model.Ticket;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepo extends JpaRepository<Ticket, UUID> {

    // Function to make sure no seat is booked twice for the exact showtime.
    boolean existsByShowtimeAndSeatNumber(Showtime show, Integer seatnumber);
}
