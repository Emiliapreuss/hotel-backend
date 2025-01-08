package com.emilia.preuss.hotelbooking.repository;

import com.emilia.preuss.hotelbooking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository  extends JpaRepository<Booking, Long> {
    List<Booking> findAllByRoomId(Long id);

    Booking findByConfirmationCode(String confirmationCode);
}
