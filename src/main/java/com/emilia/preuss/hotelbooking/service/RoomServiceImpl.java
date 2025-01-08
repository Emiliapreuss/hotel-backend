package com.emilia.preuss.hotelbooking.service;

import com.emilia.preuss.hotelbooking.exception.InternalServerException;
import com.emilia.preuss.hotelbooking.exception.ResourceNotFoundException;
import com.emilia.preuss.hotelbooking.model.Room;
import com.emilia.preuss.hotelbooking.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    public Room addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice) {
        Room.RoomBuilder roomBuilder = Room.builder()
                .type(roomType)
                .price(roomPrice);

        if(!photo.isEmpty()) {
            try {
                byte[] photoBytes = photo.isEmpty() ? null : photo.getBytes();
                Blob photoBlob = new SerialBlob(photoBytes);
                roomBuilder.photo(photoBlob);
            } catch (Exception ex) {
                log.error("Error serializing photo blob", ex);
            }
        }

        return roomRepository.save(roomBuilder.build());
    }

    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findDistinctRoomTypes();
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public byte[] getRoomPhotoById(Long id) {
        return roomRepository.findById(id)
                .map(room -> room.getPhoto())  // Extract the photo Blob from the room
                .filter(photoBlob -> photoBlob != null)  // Ensure the photoBlob is not null
                .map(photoBlob -> {
                    try {
                        return photoBlob.getBytes(1, (int) photoBlob.length());  // Get bytes from the Blob
                    } catch (SQLException e) {
                        throw new RuntimeException("Error reading photo bytes", e);  // Handle SQL exception
                    }
                })
                .orElseThrow(() -> new ResourceNotFoundException("Sorry, room with id: " + id + " not found"));
    }

    @Override
    public void deleteRoom(Long roomId) {
        Optional<Room> room = roomRepository.findById(roomId);
        if(room.isPresent()) {
            roomRepository.deleteById(roomId);
        }
    }

    @Override
    public Room updateRoom(Long roomId, String type, BigDecimal price, Blob photoBlob) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room with id "+roomId+" not found"));

        if(type != null) room.setType(type);
        if(price != null) room.setPrice(price);
        if(photoBlob != null) room.setPhoto(photoBlob);
        roomRepository.save(room);
        return room;
    }

    @Override
    public Optional<Room> getRoomById(Long roomId) {
        return roomRepository.findById(roomId);
    }
}
