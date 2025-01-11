package com.emilia.preuss.hotelbooking.service;

import com.emilia.preuss.hotelbooking.model.Booking;
import com.emilia.preuss.hotelbooking.request.BookingRequest;

import java.util.List;

public interface BookingService {
    List<Booking> getAllBookingsByRoomId(Long id);

    List<Booking> getAllBookings();

    List<Booking> getAllBokingsByUser(Long id);

    Booking findBookingByConfirmationCode(String confirmationCode);

    String saveBooking(Long roomId, BookingRequest bookingRequest);

    void cancelBooking(String confirmationCode);
}
