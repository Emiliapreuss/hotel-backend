package com.emilia.preuss.hotelbooking.controller;

import com.emilia.preuss.hotelbooking.exception.InvalidBookingRequestException;
import com.emilia.preuss.hotelbooking.exception.ResourceNotFoundException;
import com.emilia.preuss.hotelbooking.model.Booking;
import com.emilia.preuss.hotelbooking.model.Room;
import com.emilia.preuss.hotelbooking.response.BookingResponse;
import com.emilia.preuss.hotelbooking.response.RoomResponse;
import com.emilia.preuss.hotelbooking.service.BookingService;
import com.emilia.preuss.hotelbooking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final RoomService roomService;


    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        List<BookingResponse> response = bookings.stream().map(booking -> getBookingResponse(booking)).collect(toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<List<BookingResponse>> getAllBookingsByRoomId(@PathVariable Long roomId) {
        List<Booking> bookings = bookingService.getAllBookingsByRoomId(roomId);
        List<BookingResponse> response = bookings.stream().map(booking -> getBookingResponse(booking)).collect(toList());
        return ResponseEntity.ok(response);
    }


    @GetMapping("/confirmation/{confirmationCode}")
    public ResponseEntity<?> getBookingByConfirmationCode(String confirmationCode) {
        try {
            Booking booking = bookingService.findBookingByConfirmationCode(confirmationCode);
            BookingResponse response = getBookingResponse(booking);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }

    }

    @PostMapping("/room/{roomId}/booking")
    public ResponseEntity<?> saveBooking(@PathVariable Long roomId, @RequestBody Booking bookingRequest) {
        try {
            String confirmationCode = bookingService.saveBooking(roomId, bookingRequest);
            return ResponseEntity.ok("Room booked successfully, your confirmation code is: " + confirmationCode);
        }
        catch (InvalidBookingRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/booking/{bookingId}/delete")
    public void cancelBooking(Long bookingId) {
        bookingService.cancelBooking(bookingId);
    }

    private BookingResponse getBookingResponse(Booking booking) {
        Room room = roomService.getRoomById(booking.getRoom().getId()).get();
        RoomResponse roomResponse = RoomResponse.builder()
                .id(room.getId())
                .type(room.getType())
                .price(room.getPrice())
                .build();

        return BookingResponse.builder()
                .bookingId(booking.getBookingId())
                .confirmationCode(booking.getConfirmationCode())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .guestFullName(booking.getGuestFullName())
                .guestEmail(booking.getGuestEmail())
                .numOfAdults(booking.getNumOfAdults())
                .numOfChildren(booking.getNumOfChildren())
                .room(roomResponse)
                .build();

    }
}
