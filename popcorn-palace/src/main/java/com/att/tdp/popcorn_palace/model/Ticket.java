package com.att.tdp.popcorn_palace.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "ticket")
public class Ticket {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID bookingId;
    @ManyToOne(optional = false)
    private Showtime showtime;
    private Integer seatNumber;
    private String userId;

    // Null constructor
    public Ticket() {
    }

    // Constructor with args
    public Ticket(Showtime showtime, Integer seatNumber, String userId) {
        this.showtime = showtime;
        this.seatNumber = seatNumber;
        this.userId = userId;
    }

    // Setters and Getters for ticket fields
    public UUID getBookingId() {
        return bookingId;
    }

    public void setBookingId(UUID id) {
        this.bookingId = id;
    }

    public void setShowtime(Showtime showtime) {
        this.showtime = showtime;
    }

    public Showtime getShowtime() {
        return showtime;
    }

    public void setSeatNumber(Integer seatnumber) {
        this.seatNumber = seatnumber;
    }

    public Integer getSeatnumber() {
        return seatNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
