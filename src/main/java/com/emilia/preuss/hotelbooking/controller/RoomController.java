package com.emilia.preuss.hotelbooking.controller;

import com.emilia.preuss.hotelbooking.exception.PhotoRetrievalException;
import com.emilia.preuss.hotelbooking.exception.ResourceNotFoundException;
import com.emilia.preuss.hotelbooking.model.Booking;
import com.emilia.preuss.hotelbooking.model.Room;
import com.emilia.preuss.hotelbooking.response.BookingResponse;
import com.emilia.preuss.hotelbooking.response.RoomResponse;
import com.emilia.preuss.hotelbooking.service.BookingService;
import com.emilia.preuss.hotelbooking.service.RoomService;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

    private final BookingService bookingService;
    private final RoomService roomService;

    @PostMapping("add")
    public ResponseEntity<RoomResponse> addNewRoom(@RequestParam("photo") MultipartFile photo,
                                                   @RequestParam("type") String roomType,
                                                   @RequestParam("price") BigDecimal roomPrice) {
        Room newRoom = roomService.addNewRoom(photo, roomType, roomPrice);
        RoomResponse response = RoomResponse.builder()
                .id(newRoom.getId())
                .type(newRoom.getType())
                .price(newRoom.getPrice())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("room-types")
    public List<String> getRoomTypes() {
        return roomService.getAllRoomTypes();
    }

    @GetMapping()
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        List<Room> rooms = roomService.getAllRooms();
        List<RoomResponse> response = new ArrayList<>();
        for (Room room : rooms) {
            byte[] photoBytes = roomService.getRoomPhotoById(room.getId());
            if(photoBytes != null && photoBytes.length > 0) {
                String base64String = Base64.encodeBase64String(photoBytes);
                String imageType = "image/png"; // or "image/jpeg", etc.
                String base64Photo = "data:"+imageType+";base64,"+base64String;
                RoomResponse roomResponse = getRoomResponse(room);
                roomResponse.setPhoto(base64Photo);
                response.add(roomResponse);
            }
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/room/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId) {
        roomService.deleteRoom(roomId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/update/{roomId}")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long roomId,
                                                   @RequestParam(required = false) String type,
                                                   @RequestParam(required = false) BigDecimal price,
                                                   @RequestParam(required = false) MultipartFile photo) throws IOException, SQLException {

        byte[] photoBytes = photo != null && !photo.isEmpty()
                ? photo.getBytes()
                : roomService.getRoomPhotoById(roomId);
        Blob photoBlob = photoBytes != null && photoBytes.length > 0 ? new SerialBlob(photoBytes): null;
        Room room = roomService.updateRoom(roomId, type, price, photoBlob);
        RoomResponse response = getRoomResponse(room);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<Optional<RoomResponse>> getRoomById(@PathVariable Long roomId) {
        Room room = roomService.getRoomById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room "+roomId+ " not found"));
        RoomResponse roomResponse = getRoomResponse(room);
        byte[] photoBytes = roomService.getRoomPhotoById(roomId);
        if(photoBytes != null && photoBytes.length > 0) {
            String base64String = Base64.encodeBase64String(photoBytes);
            String imageType = "image/png"; // or "image/jpeg", etc.
            String base64Photo = "data:"+imageType+";base64,"+base64String;
            roomResponse.setPhoto(base64Photo);
        }

        return ResponseEntity.ok(Optional.of(roomResponse));
    }
    private RoomResponse getRoomResponse(Room room) {
        List<Booking> bookings = getAllBookingsByRoomId(room.getId());
        List<BookingResponse> bookingInfo = Optional.ofNullable(bookings)
                .orElse(Collections.emptyList())
                .stream()
                .map(booking -> BookingResponse.builder()
                                .bookingId(booking.getBookingId())
                                .confirmationCode(booking.getConfirmationCode())
                                .checkInDate(booking.getCheckInDate())
                                .checkOutDate(booking.getCheckOutDate())
                        .build())
                .toList();
//        byte[] photoBytes = null;
//        Blob photoBlob = room.getPhoto();
//        if(photoBlob != null) {
//            try {
//                photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
//            } catch (SQLException exception) {
//                throw new PhotoRetrievalException("Error retrieving photo");
//            }
//        }
        return RoomResponse.builder()
                .id(room.getId())
                .type(room.getType())
                .price(room.getPrice())
  //              .photo(photoBytes)
                .bookings(bookingInfo)
                .build();
    }

    private List<Booking> getAllBookingsByRoomId(Long id) {
        return bookingService.getAllBookingsByRoomId(id);
    }
}
