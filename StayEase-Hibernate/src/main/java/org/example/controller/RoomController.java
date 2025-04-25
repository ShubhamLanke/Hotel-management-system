package org.example.controller;

import org.example.constants.ResponseStatus;
import org.example.entity.Room;
import org.example.service.RoomService;
import org.example.utility.Response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class RoomController {
    private final RoomService roomService;
//    Response response;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    public Response addRoom(Room room) {
        roomService.addRoom(room); // update return logic
        return response = new Response(null, ResponseStatus.SUCCESS, "Room added successfully.");
    }

    public Response updateRoom(Room room) {
        Optional<Boolean> result = roomService.updateRoom(room);
        return response = result.map(success ->
                        new Response(null, ResponseStatus.SUCCESS, "Room updated successfully."))
                .orElse(new Response(null, ResponseStatus.ERROR, "Failed to update room."));
    }

    public Response getAvailableRooms() {
        List<Room> rooms = roomService.getAvailableRooms();
        return response = new Response(rooms, ResponseStatus.SUCCESS, "Available rooms retrieved successfully.");
    }

    public Response getRoomsUnderMaintenance() {
        List<Room> rooms = roomService.getRoomsUnderMaintenance();
        return response = new Response(rooms, ResponseStatus.SUCCESS, "Rooms under maintenance retrieved successfully.");
    }

    public Response markRoomUnderMaintenance(int roomId) {
        roomService.markRoomUnderMaintenance(roomId);
        return response = new Response(null, ResponseStatus.SUCCESS, "Room marked under maintenance.");
    }

    public Response markRoomAvailable(int roomId) {
        roomService.markRoomAvailable(roomId);
        return response = new Response(null, ResponseStatus.SUCCESS, "Room marked as available.");
    }

    public Response getRoomById(int roomId) {
        return response = roomService.getRoomById(roomId)
                .map(room -> new Response(room, ResponseStatus.SUCCESS, "Room found."))
                .orElse(new Response(null, ResponseStatus.ERROR, "Room not found."));
    }

    public Response getAvailableRoomsForDate(LocalDateTime checkIn, LocalDateTime checkOut) {
        try {
            List<Room> availableRooms = roomService.getAvailableRoomsForDate(checkIn, checkOut);
            if (availableRooms.isEmpty()) {
                return response = new Response(null, ResponseStatus.ERROR, "No rooms available for the given dates.");
            }
            return response = new Response(availableRooms, ResponseStatus.SUCCESS, "Available rooms fetched successfully.");
        } catch (IllegalArgumentException e) {
            return response = new Response(null, ResponseStatus.ERROR, e.getMessage());
        } catch (Exception e) {
            return response = new Response(null, ResponseStatus.ERROR, "Something went wrong while fetching available rooms.");
        }
    }
}
