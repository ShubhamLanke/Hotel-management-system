package org.example.service;

import org.example.entity.Room;

import java.util.List;
import java.util.Optional;

public interface RoomService {
    void addRoom(Room room);

    Optional<Boolean> updateRoom(Room room);

    List<Room> getAvailableRooms();

    List<Room> getRoomsUnderMaintenance();

    void markRoomUnderMaintenance(int roomId);

    void markRoomAvailable(int roomId);

    Optional<Room> getRoomById(int roomId);
}