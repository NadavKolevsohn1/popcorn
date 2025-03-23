package com.att.tdp.popcorn_palace.repo;

import com.att.tdp.popcorn_palace.model.Movie;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepo extends JpaRepository<Movie, Long> {
    // Function to find the movie by its title
    Optional<Movie> findByTitle(String title);
}
