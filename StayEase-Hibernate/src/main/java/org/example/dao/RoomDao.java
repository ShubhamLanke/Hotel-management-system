package org.example.dao;

import org.example.entity.Room;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RoomDao {
    void addRoom(Room room);

    void updateRoom(Room room);

    List<Room> getAvailableRooms();

    List<Room> getRoomsUnderMaintenance();

    void markRoomUnderMaintenance(int roomId);

    void markRoomAvailable(int roomId);

    Optional<Room> getRoomById(int roomId);

    List<Room> getAvailableRoomsForDate(LocalDateTime checkIn, LocalDateTime checkOut);
}
