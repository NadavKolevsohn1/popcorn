package com.att.tdp.popcorn_palace.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.att.tdp.popcorn_palace.model.Showtime;

import java.time.Instant;
import java.util.List;

@Repository
public interface ShowtimeRepo extends JpaRepository<Showtime, Long> {
    // Function used to validate that there are no overlapping showtimes for the
    // same theater.
    List<Showtime> findByTheaterAndStartTimeLessThanAndEndTimeGreaterThan(String theater, Instant endTime,
            Instant startTime);
}
