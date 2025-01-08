package com.emilia.preuss.hotelbooking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    private String confirmationCode;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private String guestFullName;

    private String guestEmail;

    private int numOfAdults;

    private int numOfChildren;

    public int getTotalGuestNumber() {
        return numOfAdults + numOfChildren;
    }
}