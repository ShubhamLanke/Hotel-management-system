package org.example.dao;

import org.example.constants.RoomType;
import org.example.entity.Room;
import org.example.utility.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoomDaoImpl implements RoomDao {
    private static final Logger logger = LoggerFactory.getLogger(RoomDaoImpl.class);
    private final Connection connection = DatabaseConnection.getConnection();

    @Override
    public void addRoom(Room room) {
        String sql = "INSERT INTO rooms (room_number, room_type, price, is_available) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, room.getRoomNumber());
            stmt.setString(2, room.getRoomType().name());
            stmt.setDouble(3, room.getPrice());
            stmt.setBoolean(4, room.isAvailable());
            stmt.executeUpdate();
            logger.info("Room added successfully: {}", room);
        } catch (SQLException e) {
            logger.error("Error adding room: {}", room, e);
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
            logger.info("Room updated successfully: {}", room);
        } catch (SQLException e) {
            logger.error("Error updating room: {}", room, e);
        }
    }

    @Override
    public List<Room> getAvailableRooms() {
        String sql = "SELECT * FROM rooms WHERE is_available = true ORDER BY room_id ASC";

        List<Room> availableRooms = new ArrayList<>();
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
            logger.error("Error fetching available rooms.", e);
        }
        return availableRooms;
    }

    @Override
    public List<Room> getRoomsUnderMaintenance() {
        StringBuilder sqlBuilder = new StringBuilder("SELECT r.room_id, r.room_number, r.room_type, r.price, r.is_available ");
        sqlBuilder.append("FROM rooms r ");
        sqlBuilder.append("LEFT JOIN bookings b ON r.room_id = b.room_id ");
        sqlBuilder.append("AND b.status NOT IN ('CANCELLED', 'COMPLETED') ");
        sqlBuilder.append("WHERE r.is_available = false ");
        sqlBuilder.append("AND b.booking_id IS NULL");

        // Example: Adding a condition dynamically if a room type filter is provided
        String roomTypeCondition = "suite";  // Dynamically set this value
        if (roomTypeCondition != null && !roomTypeCondition.isEmpty()) {
            sqlBuilder.append(" AND r.room_type = '").append(roomTypeCondition).append("'");
        }

        List<Room> maintenanceRooms = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sqlBuilder.toString());
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
            logger.error("Error fetching rooms under maintenance.", e);
        }
        return maintenanceRooms;
    }

    @Override
    public void markRoomUnderMaintenance(int roomId) {
        String sql = "UPDATE rooms SET is_available = false WHERE room_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            stmt.executeUpdate();
            logger.info("Room with ID {} marked as under maintenance.", roomId);
        } catch (SQLException e) {
            logger.error("Error marking room with ID {} as under maintenance.", roomId, e);
        }
    }

    @Override
    public void markRoomAvailable(int roomId) {
        String sql = "UPDATE rooms SET is_available = true WHERE room_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            stmt.executeUpdate();
            logger.info("Room with ID {} marked as available.", roomId);
        } catch (SQLException e) {
            logger.error("Error marking room with ID {} as available.", roomId, e);
        }
    }

    @Override
    public Optional<Room> getRoomById(int roomId) {
        String sql = "SELECT * FROM rooms WHERE room_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Room room = new Room(
                        rs.getInt("room_id"),
                        rs.getInt("room_number"),
                        RoomType.valueOf(rs.getString("room_type")),
                        rs.getDouble("price"),
                        rs.getBoolean("is_available")
                );
                logger.info("Fetched room with ID {}: {}", roomId, room);
                return Optional.of(room);
            }
        } catch (SQLException e) {
            logger.error("Error fetching room with ID {}", roomId, e);
        }
        logger.warn("Room with ID {} not found.", roomId);
        return Optional.empty();  // Return Optional.empty() if the room doesn't exist
    }
}
