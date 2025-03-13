package org.example.dao;

import org.example.constants.BookingStatus;
import org.example.entity.Booking;
import org.example.utility.DatabaseConnection;

import java.sql.Connection;
import java.sql.*;
import java.util.List;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookingDaoImpl implements BookingDao {
    private final Connection connection = DatabaseConnection.getConnection();
    private static final Logger logger = Logger.getLogger(BookingDaoImpl.class.getName());

    @Override
    public void createBooking(Booking booking) {
        String sql = "INSERT INTO bookings (user_id, room_id, check_in, check_out, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, booking.getUserId());
            stmt.setInt(2, booking.getRoomId());
            stmt.setTimestamp(3, Timestamp.valueOf(booking.getCheckIn()));
            stmt.setTimestamp(4, Timestamp.valueOf(booking.getCheckOut()));
            stmt.setObject(5, booking.getStatus().name(), Types.OTHER);
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    booking.setBookingId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while creating booking for user ID: " + booking.getUserId(), e);
        }
    }

    @Override
    public void updateBooking(Booking booking) { // TODO club method to single code for create/update
        String sql = "UPDATE bookings SET user_id = ?, room_id = ?, check_in = ?, check_out = ?, status = ? WHERE booking_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, booking.getUserId());
            stmt.setInt(2, booking.getRoomId());
            stmt.setTimestamp(3, Timestamp.valueOf(booking.getCheckIn()));
            stmt.setTimestamp(4, Timestamp.valueOf(booking.getCheckOut()));
            stmt.setObject(5, booking.getStatus().name(), Types.OTHER);
            stmt.setInt(6, booking.getBookingId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancelBooking(int bookingId) {
        String sql = "UPDATE bookings SET status = ? WHERE booking_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, BookingStatus.CANCELLED.name(), Types.OTHER);
            stmt.setInt(2, bookingId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Booking getBookingById(int bookingId) {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapBooking(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Booking> getBookingsByUser(int userId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE user_id = ? ORDER BY check_in DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookings.add(mapBooking(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    @Override
    public Booking getConfirmedBookingByUserId(int userId) {
        String sql = "SELECT * FROM bookings WHERE user_id = ? AND status = 'CONFIRMED'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapBooking(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Booking> getAllBookings() { // TODO String Builder
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT " +
                "b.booking_id, " +
                "u.user_id, " +
                "b.room_id, " +
                "r.room_number, " +
                "u.name, " +
                "u.email, " +
                "b.check_in, " +
                "b.check_out, " +
                "i.amount, " +
                "i.payment_status, " +
                "b.status " +
                "FROM bookings AS b " +
                "JOIN users AS u ON b.user_id = u.user_id " +
                "JOIN rooms AS r ON b.room_id = r.room_id " +
                "JOIN invoices AS i ON b.booking_id = i.booking_id " +
                "ORDER BY b.check_in DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookings.add(mapBooking(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }


    private Booking mapBooking(ResultSet rs) throws SQLException {
        return new Booking( // TODO update to encapsulation or builder pattern
                rs.getInt("booking_id"),
                rs.getInt("user_id"),
                rs.getInt("room_id"),
                rs.getTimestamp("check_in").toLocalDateTime(),
                rs.getTimestamp("check_out").toLocalDateTime(),
                BookingStatus.valueOf(rs.getString("status"))
        );
    }
}

