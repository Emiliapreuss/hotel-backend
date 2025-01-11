package com.emilia.preuss.hotelbooking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String type;

    private BigDecimal price;

    @Lob
    private Blob photo;

    //cascade - when the room is deleted, the booking will also be
    @OneToMany( fetch = LAZY,mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;

    public Room() {
        this.bookings = new ArrayList<>();
    }

    public void addBooking(Booking booking){
        bookings.add(booking);
        booking.setRoom(this);
        String bookingCode = randomNumeric(10);
        booking.setConfirmationCode(bookingCode);
    }
}
