package com.emilia.preuss.hotelbooking.controller;

import com.emilia.preuss.hotelbooking.exception.InvalidBookingRequestException;
import com.emilia.preuss.hotelbooking.exception.ResourceNotFoundException;
import com.emilia.preuss.hotelbooking.model.Booking;
import com.emilia.preuss.hotelbooking.request.BookingRequest;
import com.emilia.preuss.hotelbooking.response.BookingResponse;
import com.emilia.preuss.hotelbooking.service.BookingService;
import com.emilia.preuss.hotelbooking.utils.AuthenticationUtils;
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

    private final AuthenticationUtils authenticationUtils;
    private final BookingService bookingService;


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

    @GetMapping("/user")
    public ResponseEntity<List<BookingResponse>> getAllBookingsByUser() {
        Long userId = authenticationUtils.getAuthenticatedUser().getId();
        List<Booking> bookings = bookingService.getAllBokingsByUser(userId);
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
    public ResponseEntity<?> saveBooking(@PathVariable Long roomId, @RequestBody BookingRequest bookingRequest) {
        try {
            String confirmationCode = bookingService.saveBooking(roomId, bookingRequest);
            return ResponseEntity.ok("Room booked successfully, your confirmation code is: " + confirmationCode);
        }
        catch (InvalidBookingRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/booking/{confirmationCode}/delete")
    public void cancelBooking(@PathVariable String confirmationCode) {
        bookingService.cancelBooking(confirmationCode);
    }

    private BookingResponse getBookingResponse(Booking booking) {
        return BookingResponse.builder()
                .bookingId(booking.getBookingId())
                .confirmationCode(booking.getConfirmationCode())
                .guestEmail(booking.getUser().getEmail())
                .guestFullName(booking.getUser().getFullName())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .numOfAdults(booking.getNumOfAdults())
                .numOfChildren(booking.getNumOfChildren())
                .roomId(booking.getRoom().getId())
                .build();

    }
}
