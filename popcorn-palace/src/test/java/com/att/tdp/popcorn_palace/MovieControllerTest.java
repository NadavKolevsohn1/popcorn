package com.att.tdp.popcorn_palace;

import com.att.tdp.popcorn_palace.model.Movie;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MovieControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        // Add test
        @Test
        public void testAddMovie_Valid() throws Exception {
                Movie movie = new Movie();
                movie.setTitle("Valid Movie");
                movie.setGenre("Action");
                movie.setDuration(120);
                movie.setRating(8.5);
                movie.setReleaseYear(2025);

                String json = objectMapper.writeValueAsString(movie);
                mockMvc.perform(post("/movies")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").exists())
                                .andExpect(jsonPath("$.title").value("Valid Movie"));
        }

        // Update test
        @Test
        public void testUpdateMovie_Valid() throws Exception {
                // Add a movie first
                Movie movie = new Movie();
                movie.setTitle("Old Movie");
                movie.setGenre("Action");
                movie.setDuration(100);
                movie.setRating(7.0);
                movie.setReleaseYear(2020);

                String addJson = objectMapper.writeValueAsString(movie);
                mockMvc.perform(post("/movies")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(addJson))
                                .andExpect(status().isOk());

                // Update movie fields using the title as identifier
                Movie updateMovie = new Movie();
                updateMovie.setTitle("Updated Movie");
                updateMovie.setGenre("Comedy");
                updateMovie.setDuration(110);
                updateMovie.setRating(8.0);
                updateMovie.setReleaseYear(2021);

                String updateJson = objectMapper.writeValueAsString(updateMovie);
                mockMvc.perform(post("/movies/update/Old Movie")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateJson))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.title").value("Updated Movie"))
                                .andExpect(jsonPath("$.genre").value("Comedy"));
        }

        // Delete test
        @Test
        public void testDeleteMovie_Valid() throws Exception {
                // Add a movie to be deleted
                Movie movie = new Movie();
                movie.setTitle("Delete Movie");
                movie.setGenre("Horror");
                movie.setDuration(90);
                movie.setRating(6.0);
                movie.setReleaseYear(2021);

                String json = objectMapper.writeValueAsString(movie);
                mockMvc.perform(post("/movies")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isOk());

                // Delete the movie by title
                mockMvc.perform(delete("/movies/Delete Movie"))
                                .andExpect(status().isOk());

        }

        // Get test
        @Test
        public void testGetAllMovies() throws Exception {
                // Add a movie to ensure the list is not empty
                Movie movie = new Movie();
                movie.setTitle("Fetch Test Movie");
                movie.setGenre("Action");
                movie.setDuration(120);
                movie.setRating(8.0);
                movie.setReleaseYear(2025);

                String json = objectMapper.writeValueAsString(movie);
                mockMvc.perform(post("/movies")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isOk());

                // GET all movies and assert that one movie has the expected title
                mockMvc.perform(get("/movies/all"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.[?(@.title=='Fetch Test Movie')]").exists());
        }

        // Invalid input
        // Missing or invalid title
        @Test
        public void testAddMovie_Invalid_MissingTitle() throws Exception {
                Movie movie = new Movie();
                movie.setGenre("Action");
                movie.setDuration(120);
                movie.setRating(8.5);
                movie.setReleaseYear(2025);

                String json = objectMapper.writeValueAsString(movie);
                mockMvc.perform(post("/movies")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string(containsString("Title is required")));
        }

        // Missing or invalid genre
        @Test
        public void testAddMovie_Invalid_EmptyGenre() throws Exception {
                Movie movie = new Movie();
                movie.setTitle("Some Movie");
                movie.setGenre("   "); // Empty after trim
                movie.setDuration(120);
                movie.setRating(8.5);
                movie.setReleaseYear(2025);

                String json = objectMapper.writeValueAsString(movie);
                mockMvc.perform(post("/movies")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string(containsString("Genre is required")));
        }

        // Invalid, negative duration
        @Test
        public void testAddMovie_Invalid_NegativeDuration() throws Exception {
                Movie movie = new Movie();
                movie.setTitle("Some Movie");
                movie.setGenre("Action");
                movie.setDuration(-5);
                movie.setRating(8.5);
                movie.setReleaseYear(2025);

                String json = objectMapper.writeValueAsString(movie);
                mockMvc.perform(post("/movies")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string(containsString("Duration must be a non negative integer")));
        }

        // Invalid rating
        @Test
        public void testAddMovie_Invalid_RatingOutOfRange() throws Exception {
                Movie movie = new Movie();
                movie.setTitle("Some Movie");
                movie.setGenre("Action");
                movie.setDuration(120);
                movie.setRating(15.0); // Out of expected range
                movie.setReleaseYear(2025);

                String json = objectMapper.writeValueAsString(movie);
                mockMvc.perform(post("/movies")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string(containsString("Rating must be between 0 and 10")));
        }

        // Invalid releaseYear
        @Test
        public void testAddMovie_Invalid_MissingReleaseYear() throws Exception {
                Movie movie = new Movie();
                movie.setTitle("Some Movie");
                movie.setGenre("Action");
                movie.setDuration(120);
                movie.setRating(8.5);

                String json = objectMapper.writeValueAsString(movie);
                mockMvc.perform(post("/movies")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string(containsString("Release year is required")));
        }
}
