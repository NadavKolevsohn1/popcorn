package com.att.tdp.popcorn_palace;

import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.repo.MovieRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.Instant;
import java.util.Map;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ShowtimeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieRepo movieRepo;

    @Autowired
    private ObjectMapper objectMapper;

    private Long sampleMovieId;

    @BeforeEach
    public void setup() {
        // Create a movie to be used for showtimes
        Movie movie = new Movie();
        movie.setTitle("Showtime Test Movie");
        movie.setGenre("Drama");
        movie.setDuration(100);
        movie.setRating(7.0);
        movie.setReleaseYear(2023);
        movieRepo.save(movie);
        sampleMovieId = movie.getId();
    }

    // Add test
    @Test
    public void testAddShowtime_Valid() throws Exception {
        Map<String, Object> payload = Map.of(
                "movieId", sampleMovieId,
                "price", 20.0,
                "theater", "Theater 1",
                "startTime", Instant.now().plusSeconds(3600).toString(),
                "endTime", Instant.now().plusSeconds(7200).toString());
        String json = objectMapper.writeValueAsString(payload);
        mockMvc.perform(post("/showtimes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.theater").value("Theater 1"));
    }

    // Get test
    @Test
    public void testGetShowtimeById_Valid() throws Exception {
        // First, add a showtime
        Map<String, Object> payload = Map.of(
                "movieId", sampleMovieId,
                "price", 25.0,
                "theater", "Theater 2",
                "startTime", Instant.now().plusSeconds(3600).toString(),
                "endTime", Instant.now().plusSeconds(7200).toString());
        String json = objectMapper.writeValueAsString(payload);
        String response = mockMvc.perform(post("/showtimes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        @SuppressWarnings("unchecked")
        Map<String, Object> respMap = objectMapper.readValue(response, Map.class);
        Integer showtimeId = (Integer) respMap.get("id");

        mockMvc.perform(get("/showtimes/" + showtimeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(showtimeId));
    }

    // Update test
    @Test
    public void testUpdateShowtime_Valid() throws Exception {
        // Add a showtime first
        Map<String, Object> payload = Map.of(
                "movieId", sampleMovieId,
                "price", 30.0,
                "theater", "Theater 3",
                "startTime", Instant.now().plusSeconds(3600).toString(),
                "endTime", Instant.now().plusSeconds(7200).toString());
        String json = objectMapper.writeValueAsString(payload);
        String response = mockMvc.perform(post("/showtimes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        @SuppressWarnings("unchecked")
        Map<String, Object> respMap = objectMapper.readValue(response, Map.class);
        Integer showtimeId = (Integer) respMap.get("id");

        // Update payload
        Map<String, Object> updatePayload = Map.of(
                "movieId", sampleMovieId,
                "price", 35.0,
                "theater", "Theater 3 Updated",
                "startTime", Instant.now().plusSeconds(4000).toString(),
                "endTime", Instant.now().plusSeconds(8000).toString());
        String updateJson = objectMapper.writeValueAsString(updatePayload);

        mockMvc.perform(post("/showtimes/update/" + showtimeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.theater").value("Theater 3 Updated"))
                .andExpect(jsonPath("$.price").value(35.0));
    }

    // Delete test
    @Test
    public void testDeleteShowtime_Valid() throws Exception {
        // Add a showtime to delete
        Map<String, Object> payload = Map.of(
                "movieId", sampleMovieId,
                "price", 20.0,
                "theater", "Delete Theater",
                "startTime", Instant.now().plusSeconds(3600).toString(),
                "endTime", Instant.now().plusSeconds(7200).toString());
        String json = objectMapper.writeValueAsString(payload);
        String response = mockMvc.perform(post("/showtimes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, Object> respMap = objectMapper.readValue(response, Map.class);
        Integer showtimeId = (Integer) respMap.get("id");

        mockMvc.perform(delete("/showtimes/" + showtimeId))
                .andExpect(status().isOk());
    }

    // Invalid input

    @Test
    public void testAddShowtime_InvalidInputs() throws Exception {
        // Missing movieId
        Map<String, Object> payload1 = Map.of(
                "price", 20.0,
                "theater", "Invalid Theater",
                "startTime", Instant.now().plusSeconds(3600).toString(),
                "endTime", Instant.now().plusSeconds(7200).toString());
        String json1 = objectMapper.writeValueAsString(payload1);
        mockMvc.perform(post("/showtimes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json1))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("movieId is required")));

        // Missing price
        Map<String, Object> payload2 = Map.of(
                "movieId", sampleMovieId,
                "theater", "Invalid Theater",
                "startTime", Instant.now().plusSeconds(3600).toString(),
                "endTime", Instant.now().plusSeconds(7200).toString());
        String json2 = objectMapper.writeValueAsString(payload2);
        mockMvc.perform(post("/showtimes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json2))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("price is required")));

        // Missing theater
        Map<String, Object> payload3 = Map.of(
                "movieId", sampleMovieId,
                "price", 20.0,
                "startTime", Instant.now().plusSeconds(3600).toString(),
                "endTime", Instant.now().plusSeconds(7200).toString());
        String json3 = objectMapper.writeValueAsString(payload3);
        mockMvc.perform(post("/showtimes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json3))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("theater is required")));

        // Missing startTime
        Map<String, Object> payload4 = Map.of(
                "movieId", sampleMovieId,
                "price", 20.0,
                "theater", "Invalid Theater",
                "endTime", Instant.now().plusSeconds(7200).toString());
        String json4 = objectMapper.writeValueAsString(payload4);
        mockMvc.perform(post("/showtimes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json4))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("startTime is required")));

        // Missing endTime
        Map<String, Object> payload5 = Map.of(
                "movieId", sampleMovieId,
                "price", 20.0,
                "theater", "Invalid Theater",
                "startTime", Instant.now().plusSeconds(3600).toString());
        String json5 = objectMapper.writeValueAsString(payload5);
        mockMvc.perform(post("/showtimes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json5))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("endTime is required")));
    }

    // Test overlapping showtimes: add one and then try to add another overlapping

    @Test
    public void testAddShowtime_Overlapping() throws Exception {
        // Add first showtime
        Map<String, Object> payload1 = Map.of(
                "movieId", sampleMovieId,
                "price", 20.0,
                "theater", "Same Theater",
                "startTime", Instant.now().plusSeconds(3600).toString(),
                "endTime", Instant.now().plusSeconds(7200).toString());
        String json1 = objectMapper.writeValueAsString(payload1);
        mockMvc.perform(post("/showtimes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json1))
                .andExpect(status().isOk());

        // Add a second showtime with overlapping times in same theater
        Map<String, Object> payload2 = Map.of(
                "movieId", sampleMovieId,
                "price", 25.0,
                "theater", "Same Theater",
                "startTime", Instant.now().plusSeconds(4000).toString(),
                "endTime", Instant.now().plusSeconds(8000).toString());
        String json2 = objectMapper.writeValueAsString(payload2);
        mockMvc.perform(post("/showtimes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json2))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Overlapping showtime")));
    }
}
