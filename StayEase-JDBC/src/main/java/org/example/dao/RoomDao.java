package org.example.dao;

import org.example.entity.Room;

import java.util.List;

public interface RoomDao {
    void addRoom(Room room);

    void updateRoom(Room room);

    List<Room> getAvailableRooms();

    List<Room> getRoomsUnderMaintenance();

    void markRoomUnderMaintenance(int roomId);

    void markRoomAvailable(int roomId);

    Room getRoomById(int roomId);
}
