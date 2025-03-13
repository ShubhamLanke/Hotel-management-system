package org.example.service;

import org.example.entity.Room;

import java.util.List;

public interface RoomService {
    void addRoom(Room room);

    void updateRoom(Room room);

    List<Room> getAvailableRooms();

    List<Room> getRoomsUnderMaintenance();

    void markRoomUnderMaintenance(int roomId);

    void markRoomAvailable(int roomId);

    Room getRoomById(int roomId);
}
