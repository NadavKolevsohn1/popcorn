package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.repo.MovieRepo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/movies")
public class MovieController {
    private final MovieRepo movierepo;

    public MovieController(MovieRepo movierepo) {
        this.movierepo = movierepo;
    }

    // Get all movies
    @GetMapping("/all")
    public ResponseEntity<List<Movie>> getMovies() {
        List<Movie> movies = movierepo.findAll();
        return ResponseEntity.ok(movies);
    }

    // ADD a new movie
    @PostMapping
    public ResponseEntity<?> addMovie(@RequestBody Movie movie) {
        // validations for details provided
        if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Title is required");
        }
        if (movie.getGenre() == null || movie.getGenre().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Genre is required");
        }
        if (movie.getDuration() == null || movie.getDuration() < 0) {
            return ResponseEntity.badRequest().body("Duration must be a non negative integer");
        }
        if (movie.getRating() == null || movie.getRating() < 0 || movie.getRating() > 10) {
            return ResponseEntity.badRequest().body("Rating must be between 0 and 10");
        }
        if (movie.getReleaseYear() == null) {
            return ResponseEntity.badRequest().body("Release year is required");
        }
        Movie saved = movierepo.save(movie);
        return ResponseEntity.ok(saved);
    }

    // UPDATE a movie
    @PostMapping("/update/{movieTitle}")
    public ResponseEntity<?> updateMovie(@PathVariable String movieTitle, @RequestBody Movie update_movie) {
        Optional<Movie> optionalMovie = movierepo.findByTitle(movieTitle);
        // concerning unfound movies
        if (!optionalMovie.isPresent()) {
            return ResponseEntity.badRequest().body("Movie not found with the title" + movieTitle);
        }
        // validations for details provided
        if (update_movie.getTitle() == null || update_movie.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Title is required");
        }
        if (update_movie.getGenre() == null || update_movie.getGenre().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Genre is required");
        }
        if (update_movie.getDuration() == null || update_movie.getDuration() < 0) {
            return ResponseEntity.badRequest().body("Duration must be a non negative integer");
        }
        if (update_movie.getRating() == null || update_movie.getRating() < 0 || update_movie.getRating() > 10) {
            return ResponseEntity.badRequest().body("Rating must be between 0 and 10");
        }
        if (update_movie.getReleaseYear() == null) {
            return ResponseEntity.badRequest().body("Release year is required");
        }

        // Updating the movie and saving it
        Movie movie = optionalMovie.get();
        movie.setTitle(update_movie.getTitle());
        movie.setGenre(update_movie.getGenre());
        movie.setDuration(update_movie.getDuration());
        movie.setRating(update_movie.getRating());
        movie.setReleaseYear(update_movie.getReleaseYear());
        Movie updated = movierepo.save(movie);
        return ResponseEntity.ok(updated);
    }

    // DELETE
    @DeleteMapping("/{movieTitle}")
    public ResponseEntity<?> deleteMovie(@PathVariable String movieTitle) {
        Optional<Movie> optionalMovie = movierepo.findByTitle(movieTitle);
        // making sure the movie is in the database
        if (optionalMovie.isPresent()) {
            // deleting the movie
            movierepo.delete(optionalMovie.get());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body("Movie not found with title: " + movieTitle);
    }
}
