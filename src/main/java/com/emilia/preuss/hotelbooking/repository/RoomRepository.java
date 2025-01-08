package com.emilia.preuss.hotelbooking.repository;

import com.emilia.preuss.hotelbooking.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT DISTINCT r.type FROM Room r")
    List<String> findDistinctRoomTypes();
}
