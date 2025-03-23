package com.att.tdp.popcorn_palace.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "showtime")
public class Showtime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    @ManyToOne(optional = false)
    private Movie movie;
    private String theater;
    private Instant startTime;
    private Instant endTime;
    private Double price;

    // Null constructor
    public Showtime() {
    }

    // Constructor with args
    public Showtime(Movie movie, String theater, Instant startTime, Instant endTime, Double price) {
        this.movie = movie;
        this.theater = theater;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
    }

    // Setters and Getters for showTime fields
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setTheater(String theater) {
        this.theater = theater;
    }

    public String getTheater() {
        return theater;
    }

    public void setStartTime(Instant start) {
        this.startTime = start;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setEndTime(Instant end) {
        this.endTime = end;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPrice() {
        return price;
    }
}
