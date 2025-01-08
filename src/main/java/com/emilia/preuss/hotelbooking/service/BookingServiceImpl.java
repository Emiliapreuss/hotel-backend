package com.emilia.preuss.hotelbooking.service;

import com.emilia.preuss.hotelbooking.exception.InvalidBookingRequestException;
import com.emilia.preuss.hotelbooking.model.Booking;
import com.emilia.preuss.hotelbooking.model.Room;
import com.emilia.preuss.hotelbooking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomService roomService;

    @Override
    public List<Booking> getAllBookingsByRoomId(Long id) {
        return bookingRepository.findAllByRoomId(id);
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking findBookingByConfirmationCode(String confirmationCode) {
       return bookingRepository.findByConfirmationCode(confirmationCode);
    }

    @Override
    public String saveBooking(Long roomId, Booking bookingRequest) {
        if(bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
            throw new InvalidBookingRequestException("Check-in date must be before check out date");
        }
        Room room = roomService.getRoomById(roomId).get();
        List<Booking> existingBookings = room.getBookings();
        boolean roomIsAvailable = isRoomAvailable(existingBookings, bookingRequest);
        if(roomIsAvailable) {
            room.addBooking(bookingRequest);
            bookingRepository.save(bookingRequest);
        } else {
            throw new InvalidBookingRequestException("This room is already booked for selected dates");
        }
        return bookingRequest.getConfirmationCode();
    }


    // 8:52
    private boolean isRoomAvailable(List<Booking> bookings, Booking bookingRequest) {
        return bookings.stream()
                .noneMatch(existingBooking ->
                        !(bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate()) ||
                                bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))
                );
    }

    @Override
    public void cancelBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }
}
