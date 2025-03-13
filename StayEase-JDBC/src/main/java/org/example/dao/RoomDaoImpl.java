package org.example.dao;

import org.example.constants.RoomType;
import org.example.entity.Room;
import org.example.utility.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDaoImpl implements RoomDao {
    private final Connection connection = DatabaseConnection.getConnection();

    @Override
    public void addRoom(Room room) {
        String sql = "INSERT INTO rooms (room_number, type, price, is_available) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, room.getRoomNumber());
            stmt.setString(2, room.getRoomType().name());
            stmt.setDouble(3, room.getPrice());
            stmt.setBoolean(4, room.isAvailable());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateRoom(Room room) {
        String sql = "UPDATE rooms SET room_number = ?, room_type = ?, price = ?, is_available = ? WHERE room_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, room.getRoomNumber());
            stmt.setObject(2, room.getRoomType().name(), Types.OTHER);
            stmt.setDouble(3, room.getPrice());
            stmt.setBoolean(4, room.isAvailable());
            stmt.setInt(5, room.getRoomID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Room> getAvailableRooms() {
        List<Room> availableRooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE is_available = true";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                availableRooms.add(new Room(
                        rs.getInt("room_id"),
                        rs.getInt("room_number"),
                        RoomType.valueOf(rs.getString("room_type")),
                        rs.getDouble("price"),
                        rs.getBoolean("is_available")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return availableRooms;
    }

    @Override
    public List<Room> getRoomsUnderMaintenance() {
        List<Room> maintenanceRooms = new ArrayList<>();
        String sql = "SELECT r.room_id, r.room_number, r.room_type, r.price, r.is_available " +
                "FROM rooms r " +
                "LEFT JOIN bookings b ON r.room_id = b.room_id " +
                "   AND b.status NOT IN ('CANCELLED', 'COMPLETED') " +
                "WHERE r.is_available = false " +
                "  AND b.booking_id IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                maintenanceRooms.add(new Room(
                        rs.getInt("room_id"),
                        rs.getInt("room_number"),
                        RoomType.valueOf(rs.getString("room_type")),
                        rs.getDouble("price"),
                        rs.getBoolean("is_available")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maintenanceRooms;
    }


    @Override
    public void markRoomUnderMaintenance(int roomId) {
        String sql = "UPDATE rooms SET is_available = false WHERE room_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void markRoomAvailable(int roomId) {
        String sql = "UPDATE rooms SET is_available = true WHERE room_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Room getRoomById(int roomId) {
        String sql = "SELECT * FROM rooms WHERE room_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Room(
                        rs.getInt("room_id"),
                        rs.getInt("room_number"),
                        RoomType.valueOf(rs.getString("room_type")),
                        rs.getDouble("price"),
                        rs.getBoolean("is_available")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
