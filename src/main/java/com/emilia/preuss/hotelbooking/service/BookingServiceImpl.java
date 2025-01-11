package com.emilia.preuss.hotelbooking.service;

import com.emilia.preuss.hotelbooking.exception.InvalidBookingRequestException;
import com.emilia.preuss.hotelbooking.mapper.BookingMapper;
import com.emilia.preuss.hotelbooking.model.Booking;
import com.emilia.preuss.hotelbooking.model.Room;
import com.emilia.preuss.hotelbooking.model.User;
import com.emilia.preuss.hotelbooking.repository.BookingRepository;
import com.emilia.preuss.hotelbooking.request.BookingRequest;
import com.emilia.preuss.hotelbooking.utils.AuthenticationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final AuthenticationUtils authenticationUtils;
    private final BookingMapper bookingMapper;
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
    public List<Booking> getAllBokingsByUser(Long id) {
        return bookingRepository.findAllByUserId(id);
    }

    @Override
    public Booking findBookingByConfirmationCode(String confirmationCode) {
       return bookingRepository.findByConfirmationCode(confirmationCode);
    }

    @Override
    public String saveBooking(Long roomId, BookingRequest bookingRequest) {
        User user = authenticationUtils.getAuthenticatedUser();
        Booking booking = bookingMapper.mapBookingRequestToEntity(bookingRequest, user);
        if(bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
            throw new InvalidBookingRequestException("Check-in date must be before check out date");
        }
        Room room = roomService.getRoomById(roomId).get();
        List<Booking> existingBookings = room.getBookings();
        boolean roomIsAvailable = isRoomAvailable(existingBookings, booking);
        if(roomIsAvailable) {
            room.addBooking(booking);
            bookingRepository.save(booking);
        } else {
            throw new InvalidBookingRequestException("This room is already booked for selected dates");
        }
        return booking.getConfirmationCode();
    }

    private boolean isRoomAvailable(List<Booking> bookings, Booking bookingRequest) {
        return bookings.stream()
                .noneMatch(existingBooking ->
                        !(bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate()) ||
                                bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))
                );
    }

    @Override
    @Transactional
    public void cancelBooking(String confirmationCode) {
        bookingRepository.deleteByConfirmationCode(confirmationCode);
    }
}
