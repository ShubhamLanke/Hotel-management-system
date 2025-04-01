package org.example.controller;

import org.example.constants.ResponseStatus;
import org.example.entity.Room;
import org.example.service.RoomService;
import org.example.utility.Response;

import java.util.List;
import java.util.Optional;

public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    public Response addRoom(Room room) {
        roomService.addRoom(room);
        return new Response(null, ResponseStatus.SUCCESS, "Room added successfully.");
    }

    public Response updateRoom(Room room) {
        Optional<Boolean> result = roomService.updateRoom(room);
        return result.map(success ->
                        new Response(null, ResponseStatus.SUCCESS, "Room updated successfully."))
                .orElse(new Response(null, ResponseStatus.ERROR, "Failed to update room."));
    }

    public Response getAvailableRooms() {
        List<Room> rooms = roomService.getAvailableRooms();
        return new Response(rooms, ResponseStatus.SUCCESS, "Available rooms retrieved successfully.");
    }

    public Response getRoomsUnderMaintenance() {
        List<Room> rooms = roomService.getRoomsUnderMaintenance();
        return new Response(rooms, ResponseStatus.SUCCESS, "Rooms under maintenance retrieved successfully.");
    }

    public Response markRoomUnderMaintenance(int roomId) {
        roomService.markRoomUnderMaintenance(roomId);
        return new Response(null, ResponseStatus.SUCCESS, "Room marked under maintenance.");
    }

    public Response markRoomAvailable(int roomId) {
        roomService.markRoomAvailable(roomId);
        return new Response(null, ResponseStatus.SUCCESS, "Room marked as available.");
    }

    public Response getRoomById(int roomId) {
        return roomService.getRoomById(roomId)
                .map(room -> new Response(room, ResponseStatus.SUCCESS, "Room found."))
                .orElse(new Response(null, ResponseStatus.ERROR, "Room not found."));
    }
}
