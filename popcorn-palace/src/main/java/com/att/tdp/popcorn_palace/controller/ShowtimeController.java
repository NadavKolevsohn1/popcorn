package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repo.MovieRepo;
import com.att.tdp.popcorn_palace.repo.ShowtimeRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/showtimes")

public class ShowtimeController {
    private final ShowtimeRepo showtimeRepo;
    private final MovieRepo movieRepo;

    public ShowtimeController(ShowtimeRepo showtimeRepo, MovieRepo movieRepo) {
        this.showtimeRepo = showtimeRepo;
        this.movieRepo = movieRepo;
    }

    // Get the showtime by its id
    @GetMapping("/{showtimeId}")
    public ResponseEntity<?> getShowtime(@PathVariable("showtimeId") Long id) {
        Optional<Showtime> optional_Showtime = showtimeRepo.findById(id);
        // Checking that the provided id appears
        if (!optional_Showtime.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Showtime showtime = optional_Showtime.get();
        Map<String, Object> response = Map.of(
                "id", showtime.getId(),
                "price", showtime.getPrice(),
                "movieId", showtime.getMovie().getId(),
                "theater", showtime.getTheater(),
                "startTime", showtime.getStartTime(),
                "endTime", showtime.getEndTime());
        return ResponseEntity.ok(response);

    }

    // Adding a showtime
    @PostMapping
    public ResponseEntity<?> addShowtime(@RequestBody Map<String, Object> payload) {
        // Validate and extract the movieId
        if (!payload.containsKey("movieId") || payload.get("movieId") == null) {
            return ResponseEntity.badRequest().body("movieId is required");
        }
        Long movieId;
        try {
            movieId = Long.parseLong(payload.get("movieId").toString());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid movieId");
        }
        Optional<Movie> optional_Movie = movieRepo.findById(movieId);
        if (!optional_Movie.isPresent()) {
            return ResponseEntity.badRequest().body("Movie not found with id: " + movieId);
        }

        // Validate and Set price
        if (!payload.containsKey("price") || payload.get("price") == null) {
            return ResponseEntity.badRequest().body("price is required");
        }
        Double price;
        try {
            price = Double.parseDouble(payload.get("price").toString());
            if (price <= 0) {
                return ResponseEntity.badRequest().body("Price must be positive");
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid price");
        }

        // Validate and Set theater
        if (!payload.containsKey("theater") || payload.get("theater") == null
                || payload.get("theater").toString().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("theater is required");
        }
        String theater = payload.get("theater").toString();

        // Validate and Set startTime and endTime
        if (!payload.containsKey("startTime") || payload.get("startTime") == null) {
            return ResponseEntity.badRequest().body("startTime is required");
        }
        if (!payload.containsKey("endTime") || payload.get("endTime") == null) {
            return ResponseEntity.badRequest().body("endTime is required");
        }
        Instant startTime, endTime;
        try {
            startTime = Instant.parse(payload.get("startTime").toString());
            endTime = Instant.parse(payload.get("endTime").toString());
            if (!endTime.isAfter(startTime)) {
                return ResponseEntity.badRequest().body("endTime must be after startTime");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid time format");
        }
        // Making sure no overlapping showtimes for same theater
        List<Showtime> overlapping = showtimeRepo.findByTheaterAndStartTimeLessThanAndEndTimeGreaterThan(theater,
                endTime, startTime);
        if (!overlapping.isEmpty()) {
            return ResponseEntity.badRequest().body("Overlapping showtime in " + theater);
        }
        // Adding the showtime requires and saving it
        Showtime showtime = new Showtime();
        showtime.setMovie(optional_Movie.get());
        showtime.setPrice(price);
        showtime.setTheater(theater);
        showtime.setStartTime(startTime);
        showtime.setEndTime(endTime);

        Showtime saved = showtimeRepo.save(showtime);
        Map<String, Object> response = Map.of(
                "id", saved.getId(),
                "price", saved.getPrice(),
                "movieId", saved.getMovie().getId(),
                "theater", saved.getTheater(),
                "startTime", saved.getStartTime(),
                "endTime", saved.getEndTime());
        return ResponseEntity.ok(response);
    }

    // UPDATE a showtime by showtimeId
    @PostMapping("/update/{showtimeId}")
    public ResponseEntity<?> updateShowtime(@PathVariable Long showtimeId, @RequestBody Map<String, Object> payload) {
        // Validating showtime exists
        Optional<Showtime> optional_exist = showtimeRepo.findById(showtimeId);
        if (!optional_exist.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Showtime existing = optional_exist.get();

        // Validate and extract movieId
        if (!payload.containsKey("movieId") || payload.get("movieId") == null) {
            return ResponseEntity.badRequest().body("movieId is required");
        }
        Long movieId;
        try {
            movieId = Long.parseLong(payload.get("movieId").toString());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid movieId");
        }
        Optional<Movie> optional_movie = movieRepo.findById(movieId);
        if (!optional_movie.isPresent()) {
            return ResponseEntity.badRequest().body("Movie not found with id: " + movieId);
        }

        // Validate and Set price
        if (!payload.containsKey("price") || payload.get("price") == null) {
            return ResponseEntity.badRequest().body("price is required");
        }
        Double price;
        try {
            price = Double.parseDouble(payload.get("price").toString());
            if (price <= 0) {
                return ResponseEntity.badRequest().body("Price must be positive");
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid price");
        }

        // Validate and Set theater
        if (!payload.containsKey("theater") || payload.get("theater") == null
                || payload.get("theater").toString().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("theater is required");
        }
        String theater = payload.get("theater").toString();

        // Validate and Set startTime and endTime
        if (!payload.containsKey("startTime") || payload.get("startTime") == null) {
            return ResponseEntity.badRequest().body("startTime is required");
        }
        if (!payload.containsKey("endTime") || payload.get("endTime") == null) {
            return ResponseEntity.badRequest().body("endTime is required");
        }
        Instant startTime, endTime;
        try {
            startTime = Instant.parse(payload.get("startTime").toString());
            endTime = Instant.parse(payload.get("endTime").toString());
            if (!endTime.isAfter(startTime)) {
                return ResponseEntity.badRequest().body("endTime must be after startTime");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid time format. Expected ISO-8601 format.");
        }

        // Making sure no overlapping showtimes for same theater (exclude current
        // showtime)
        List<Showtime> overlapping = showtimeRepo.findByTheaterAndStartTimeLessThanAndEndTimeGreaterThan(theater,
                endTime, startTime);
        overlapping.removeIf(s -> s.getId().equals(showtimeId));
        if (!overlapping.isEmpty()) {
            return ResponseEntity.badRequest().body("Overlapping showtime in the same theater");
        }

        // Updating the fields
        existing.setMovie(optional_movie.get());
        existing.setPrice(price);
        existing.setTheater(theater);
        existing.setStartTime(startTime);
        existing.setEndTime(endTime);

        Showtime updated = showtimeRepo.save(existing);
        return ResponseEntity.ok(updated);
    }

    // DELETE
    @DeleteMapping("/{showtimeId}")
    public ResponseEntity<?> deleteShowtime(@PathVariable Long showtimeId) {
        Optional<Showtime> optShowtime = showtimeRepo.findById(showtimeId);
        // Making sure the showtime is in the database
        if (!optShowtime.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        // Deleting
        showtimeRepo.delete(optShowtime.get());
        return ResponseEntity.ok().build();
    }
}
