package com.att.tdp.popcorn_palace.repo;

import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.time.Instant;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ShowtimeRepoTest {

    @Autowired
    private ShowtimeRepo showtimeRepo;

    @Autowired
    private MovieRepo movieRepo;

    @Test
    void testOverlappingShowtimeQuery() {
        // Create and save a movie
        Movie movie = new Movie("Overlap Test Movie", "Drama", 100, 7.0, 2023);
        movieRepo.save(movie);

        // Create a showtime from 10:00 to 12:00 UTC on Jan 1, 2025
        Showtime s = new Showtime();
        s.setMovie(movie);
        s.setTheater("Test Theater");
        s.setStartTime(Instant.parse("2025-01-01T10:00:00Z"));
        s.setEndTime(Instant.parse("2025-01-01T12:00:00Z"));
        s.setPrice(12.0);
        showtimeRepo.save(s);

        // Query for overlapping: using a time window from 09:00 to 11:00 should overlap
        List<Showtime> overlapping = showtimeRepo.findByTheaterAndStartTimeLessThanAndEndTimeGreaterThan(
                "Test Theater", Instant.parse("2025-01-01T11:00:00Z"), Instant.parse("2025-01-01T09:00:00Z"));
        assertThat(overlapping).hasSize(1);

        // Query for non-overlapping: using a time window completely before the showtime
        List<Showtime> nonOverlapping = showtimeRepo.findByTheaterAndStartTimeLessThanAndEndTimeGreaterThan(
                "Test Theater", Instant.parse("2025-01-01T09:00:00Z"), Instant.parse("2025-01-01T07:00:00Z"));
        assertThat(nonOverlapping).isEmpty();
    }
}
