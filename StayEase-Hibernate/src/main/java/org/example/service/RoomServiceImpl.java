package org.example.service;

import lombok.extern.log4j.Log4j2;
import org.example.dao.RoomDao;
import org.example.entity.Room;
import org.example.exception.RoomNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Log4j2
public class RoomServiceImpl implements RoomService {
    private final RoomDao roomDao;

    public RoomServiceImpl(RoomDao roomDao) {
        this.roomDao = roomDao;
    }

    @Override
    public void addRoom(Room room) {
        if (room == null) {
            throw new IllegalArgumentException("Room cannot be null.");
        }
        if (room.getRoomNumber() <= 0) {
            throw new IllegalArgumentException("Room number must be a positive integer.");
        }
        roomDao.addRoom(room);
        log.info("Room added successfully: {}", room);
    }

    @Override
    public Optional<Boolean> updateRoom(Room room) {
        if (room == null || room.getRoomID() <= 0) {
            throw new IllegalArgumentException("Room or Room ID is invalid.");
        }
        Optional<Room> existingRoom = roomDao.getRoomById(room.getRoomID());
        if (existingRoom.isEmpty()) {
            return Optional.empty();
        }
        if (room.getRoomNumber() <= 0) {
            throw new IllegalArgumentException("Room number must be a positive integer.");
        }

        try {
            roomDao.updateRoom(room);
            log.info("Room updated successfully: {}", room);
            return Optional.of(true);
        } catch (Exception e) {
            log.error("Failed to update room with ID {}: {}", room.getRoomID(), e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public List<Room> getAvailableRooms() {
        return roomDao.getAvailableRooms();
    }

    @Override
    public List<Room> getAvailableRoomsForDate(LocalDateTime checkIn, LocalDateTime checkOut) {
        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Invalid date range. Check-out must be after check-in.");
        }
        return roomDao.getAvailableRoomsForDate(checkIn, checkOut);
    }

    @Override
    public List<Room> getRoomsUnderMaintenance() {
        return roomDao.getRoomsUnderMaintenance();
    }

    @Override
    public void markRoomUnderMaintenance(int roomId) {
        if (roomId <= 0) {
            throw new IllegalArgumentException("Invalid Room ID.");
        }
        Optional<Room> room = roomDao.getRoomById(roomId);
        if (room.isEmpty()) {
            throw new RoomNotFoundException("Room with ID " + roomId + " not found.");
        }

        try {
            roomDao.markRoomUnderMaintenance(roomId);
            log.info("Room with ID {} marked as under maintenance.", roomId);
        } catch (Exception e) {
            log.error("Failed to mark room with ID {} as under maintenance: {}", roomId, e.getMessage(), e);
            throw new RuntimeException("Failed to mark room as under maintenance.", e);
        }
    }

    @Override
    public void markRoomAvailable(int roomId) {
        if (roomId <= 0) {
            throw new IllegalArgumentException("Invalid Room ID.");
        }
        Optional<Room> room = roomDao.getRoomById(roomId);
        if (room.isEmpty()) {
            throw new RoomNotFoundException("Room with ID " + roomId + " not found.");
        }

        try {
            roomDao.markRoomAvailable(roomId);
            log.info("Room with ID {} marked as available.", roomId);
        } catch (Exception e) {
            log.error("Failed to mark room with ID {} as available: {}", roomId, e.getMessage(), e);
            throw new RuntimeException("Failed to mark room as available.", e);
        }
    }

    @Override
    public Optional<Room> getRoomById(int roomId) {
        if (roomId <= 0) {
            throw new IllegalArgumentException("Invalid Room ID.");
        }
        Optional<Room> room = roomDao.getRoomById(roomId);
        if (room.isPresent()) {
            log.info("Fetched room with ID {}: {}", roomId, room);
        } else {
            log.warn("Room with ID {} not found.", roomId);
        }
        return room;
    }
}
