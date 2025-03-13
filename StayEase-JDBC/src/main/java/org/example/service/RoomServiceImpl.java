package org.example.service;

import org.example.dao.RoomDao;
import org.example.entity.Room;

import java.util.List;

public class RoomServiceImpl implements RoomService {
    private final RoomDao roomDao;

    public RoomServiceImpl(RoomDao roomDao) {
        this.roomDao = roomDao;
    }

    @Override
    public void addRoom(Room room) {
        roomDao.addRoom(room);
    }

    @Override
    public void updateRoom(Room room) {
        roomDao.updateRoom(room);
    }

    @Override
    public List<Room> getAvailableRooms() {
        return roomDao.getAvailableRooms();
    }

    @Override
    public List<Room> getRoomsUnderMaintenance() {
        return roomDao.getRoomsUnderMaintenance();
    }

    @Override
    public void markRoomUnderMaintenance(int roomId) {
        roomDao.markRoomUnderMaintenance(roomId);
    }

    @Override
    public void markRoomAvailable(int roomId) {
        roomDao.markRoomAvailable(roomId);
    }

    @Override
    public Room getRoomById(int roomId) {
        return roomDao.getRoomById(roomId);
    }
}
