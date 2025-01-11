package com.emilia.preuss.hotelbooking.mapper;

import com.emilia.preuss.hotelbooking.model.Booking;
import com.emilia.preuss.hotelbooking.model.User;
import com.emilia.preuss.hotelbooking.request.BookingRequest;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public Booking mapBookingRequestToEntity(BookingRequest bookingRequest, User user) {
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setCheckInDate(bookingRequest.getCheckInDate());
        booking.setCheckOutDate(bookingRequest.getCheckOutDate());
        booking.setNumOfAdults(bookingRequest.getNumOfAdults());
        booking.setNumOfChildren(bookingRequest.getNumOfChildren());
        return booking;
    }

//    public BookingResponse toDto(Booking booking) {
//        return BookingResponse.builder()
//                .bookingId(booking.getId())
//                .confirmationCode(booking.getConfirmationCode())
//                .checkInDate(booking.getCheckInDate())
//                .checkOutDate(booking.getCheckOutDate())
//                .guestFullName(booking.getGuestFullName())
//                .guestEmail(booking.getGuestEmail())
//                .numOfAdults(booking.getNumOfAdults())
//                .numOfChildren(booking.getNumOfChildren())
//                .room(new RoomResponse(booking.getRoomId())) // Assuming RoomResponse needs roomId
//                .build();
//    }
}

