package com.emilia.preuss.hotelbooking.service;

import com.emilia.preuss.hotelbooking.model.Booking;

import java.util.List;

public interface BookingService {
    List<Booking> getAllBookingsByRoomId(Long id);

    List<Booking> getAllBookings();

    Booking findBookingByConfirmationCode(String confirmationCode);

    String saveBooking(Long roomId, Booking bookingRequest);

    void cancelBooking(Long bookingId);
}
