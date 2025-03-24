package org.example.controller;

import org.example.entity.Room;
import org.example.service.RoomService;

import java.util.List;
import java.util.Optional;

public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    public void addRoom(Room room) {
        roomService.addRoom(room);
    }

    public boolean updateRoom(Room room) {
        Optional<Boolean> result = roomService.updateRoom(room);
        return result.orElse(false);
    }

    public List<Room> getAvailableRooms() {
        return roomService.getAvailableRooms();
    }

    public List<Room> getRoomsUnderMaintenance() {
        return roomService.getRoomsUnderMaintenance();
    }

    public void markRoomUnderMaintenance(int roomId) {
        roomService.markRoomUnderMaintenance(roomId);
    }

    public void markRoomAvailable(int roomId) {
        roomService.markRoomAvailable(roomId);
    }

    public Room getRoomById(int roomId) {
        return roomService.getRoomById(roomId);
    }
}
