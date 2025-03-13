package org.example.controller;

import org.example.entity.Room;
import org.example.service.RoomService;

import java.util.List;

public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    public void addRoom(Room room) {
        roomService.addRoom(room);
        System.out.println("Room added successfully!");
    }

    public boolean updateRoom(Room room) {
        try {
            roomService.updateRoom(room);
            System.out.println("Room updated successfully!");
            return true;
        } catch (Exception e) {
            System.out.println("Failed to update room: " + e.getMessage());
            return false;
        }
    }

    public List<Room> getAvailableRooms() {
        return roomService.getAvailableRooms();
    }

    public List<Room> getRoomsUnderMaintenance() {
        return roomService.getRoomsUnderMaintenance();
    }

    public void markRoomUnderMaintenance(int roomId) {
        roomService.markRoomUnderMaintenance(roomId);
        System.out.println("Room marked under maintenance.");
    }

    public void markRoomAvailable(int roomId) {
        roomService.markRoomAvailable(roomId);
        System.out.println("Room is available now.");
    }

    public Room getRoomById(int roomId) {
        return roomService.getRoomById(roomId);
    }
}
