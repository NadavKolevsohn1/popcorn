package com.att.tdp.popcorn_palace;

import com.att.tdp.popcorn_palace.repo.MovieRepo;
import com.att.tdp.popcorn_palace.repo.ShowtimeRepo;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MovieRepo movieRepo;

    @Autowired
    private ShowtimeRepo showtimeRepo;

    private Long sampleShowtimeId;

    @BeforeEach
    public void setup() {
        // Create a movie and a showtime for booking tests
        var movie = new com.att.tdp.popcorn_palace.model.Movie();
        movie.setTitle("Booking Test Movie");
        movie.setGenre("Comedy");
        movie.setDuration(90);
        movie.setRating(7.2);
        movie.setReleaseYear(2024);
        movieRepo.save(movie);

        var showtime = new com.att.tdp.popcorn_palace.model.Showtime();
        showtime.setMovie(movie);
        showtime.setTheater("Booking Theater");
        showtime.setStartTime(Instant.now().plusSeconds(3600));
        showtime.setEndTime(Instant.now().plusSeconds(7200));
        showtime.setPrice(15.0);
        showtimeRepo.save(showtime);

        sampleShowtimeId = showtime.getId();
    }

    // Valid Booking
    @Test
    public void testBookTicket_Valid() throws Exception {
        Map<String, Object> payload = Map.of(
                "showtimeId", sampleShowtimeId,
                "seatNumber", 5,
                "userId", "valid-user");
        String json = objectMapper.writeValueAsString(payload);
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").exists());
    }

    // Prevent Double Booking
    @Test
    public void testBookTicket_DoubleBooking() throws Exception {
        Map<String, Object> payload = Map.of(
                "showtimeId", sampleShowtimeId,
                "seatNumber", 10,
                "userId", "first-user");
        String json = objectMapper.writeValueAsString(payload);
        // First booking should succeed
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").exists());

        // Second booking with same seatNumber should fail
        Map<String, Object> payload2 = Map.of(
                "showtimeId", sampleShowtimeId,
                "seatNumber", 10,
                "userId", "second-user");
        String json2 = objectMapper.writeValueAsString(payload2);
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json2))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("already booked")));
    }

    // Invalid Booking Inputs
    // Missing userId
    @Test
    public void testBookTicket_Invalid_MissingUserId() throws Exception {
        Map<String, Object> payload = Map.of(
                "showtimeId", sampleShowtimeId,
                "seatNumber", 5);
        String json = objectMapper.writeValueAsString(payload);
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Missing required fields")));
    }

    // Invalid showtime ID
    @Test
    public void testBookTicket_Invalid_NonNumericShowtimeId() throws Exception {
        Map<String, Object> payload = Map.of(
                "showtimeId", "non-numeric",
                "seatNumber", 5,
                "userId", "test-user");
        String json = objectMapper.writeValueAsString(payload);
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid showtimeId")));
    }

    // Invalid seatNumber
    @Test
    public void testBookTicket_Invalid_NonNumericSeatNumber() throws Exception {
        Map<String, Object> payload = Map.of(
                "showtimeId", sampleShowtimeId,
                "seatNumber", "invalid",
                "userId", "test-user");
        String json = objectMapper.writeValueAsString(payload);
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid seatNumber")));
    }
}
