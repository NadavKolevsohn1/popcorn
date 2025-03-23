package com.att.tdp.popcorn_palace.repo;

import com.att.tdp.popcorn_palace.model.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MovieRepoTest {

    @Autowired
    private MovieRepo movieRepo;

    @Test
    void testFindByTitle_Found() {
        Movie movie = new Movie("Repo Test Movie", "Action", 120, 8.5, 2025);
        movieRepo.save(movie);

        Optional<Movie> found = movieRepo.findByTitle("Repo Test Movie");
        assertThat(found).isPresent();
        assertThat(found.get().getGenre()).isEqualTo("Action");
    }

    @Test
    void testFindByTitle_NotFound() {
        Optional<Movie> found = movieRepo.findByTitle("Non-Existent Movie");
        assertThat(found).isNotPresent();
    }
}
