package org.example.wrapperclass;

import org.example.entity.Room;

import java.time.LocalDateTime;
import java.util.List;

public class RoomAvailabilityResult {
    private List<Room> availableRooms;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;

    public RoomAvailabilityResult(List<Room> availableRooms, LocalDateTime checkInDate, LocalDateTime checkOutDate) {
        this.availableRooms = availableRooms;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    public List<Room> getAvailableRooms() {
        return availableRooms;
    }

    public LocalDateTime getCheckInDate() {
        return checkInDate;
    }

    public LocalDateTime getCheckOutDate() {
        return checkOutDate;
    }
}
