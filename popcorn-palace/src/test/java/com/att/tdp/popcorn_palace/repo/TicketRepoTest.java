package com.att.tdp.popcorn_palace.repo;

import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.model.Ticket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TicketRepoTest {

    @Autowired
    private TicketRepo ticketRepo;

    @Autowired
    private ShowtimeRepo showtimeRepo;

    @Autowired
    private MovieRepo movieRepo;

    @Test
    void testExistsByShowtimeAndSeatNumber() {
        // Create a movie
        Movie movie = new Movie("Ticket Test Movie", "Comedy", 90, 7.2, 2024);
        movieRepo.save(movie);

        // Create a showtime
        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setTheater("Ticket Theater");
        showtime.setStartTime(Instant.now().plusSeconds(3600));
        showtime.setEndTime(Instant.now().plusSeconds(7200));
        showtime.setPrice(15.0);
        showtimeRepo.save(showtime);

        // Create a ticket for seat 10
        Ticket ticket = new Ticket();
        ticket.setShowtime(showtime);
        ticket.setSeatNumber(10);
        ticket.setUserId("test-user");
        ticketRepo.save(ticket);

        // Verify the ticket exists for seat 10
        boolean exists = ticketRepo.existsByShowtimeAndSeatNumber(showtime, 10);
        assertThat(exists).isTrue();

        // Verify a seat that is not booked returns false
        boolean notExists = ticketRepo.existsByShowtimeAndSeatNumber(showtime, 20);
        assertThat(notExists).isFalse();
    }
}
