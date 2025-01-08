package com.emilia.preuss.hotelbooking.service;

import com.emilia.preuss.hotelbooking.model.Room;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface RoomService {

    Room addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice);

    List<String> getAllRoomTypes();

    List<Room> getAllRooms();

    byte[] getRoomPhotoById(Long id);

    void deleteRoom(Long roomId);

    Room updateRoom(Long roomId, String type, BigDecimal price, Blob photoBlob);

    Optional<Room> getRoomById(Long roomId);
}
