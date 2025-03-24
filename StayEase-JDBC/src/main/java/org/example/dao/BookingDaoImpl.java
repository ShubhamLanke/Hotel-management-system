package org.example.dao;

import org.example.constants.BookingStatus;
import org.example.entity.Booking;
import org.example.utility.DatabaseConnection;

import java.sql.Connection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookingDaoImpl implements BookingDao {
    private final Connection connection = DatabaseConnection.getConnection();
    private static final Logger logger = Logger.getLogger(BookingDaoImpl.class.getName());

    @Override
    public void createBooking(Booking booking) {
        try {
            saveBooking(booking, true); // Create new booking
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while creating booking for user ID: " + booking.getUserId(), e);
        }
    }

    @Override
    public void updateBooking(Booking booking) {
        try {
            saveBooking(booking, false); // Update existing booking
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while updating booking ID: " + booking.getBookingId(), e);
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
            logger.log(Level.SEVERE, "Error while canceling booking ID: " + bookingId, e);
        }
    }

    @Override
    public Optional<Booking> getBookingById(int bookingId) {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapBooking(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching booking by ID: " + bookingId, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Booking> getBookingsByUser(int userId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE user_id = ? ORDER BY check_in DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapBooking(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching bookings for user ID: " + userId, e);
        }
        return bookings;
    }

    @Override
    public Optional<Booking> getConfirmedBookingByUserId(int userId) {
        String sql = "SELECT * FROM bookings WHERE user_id = ? AND status = 'CONFIRMED'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapBooking(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error occurred while fetching confirmed booking for user ID: " + userId, e);
        }
        return Optional.empty(); // Return an empty Optional when no booking is found
    }

    @Override
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
                .append("b.booking_id, ")
                .append("u.user_id, ")
                .append("b.room_id, ")
                .append("r.room_number, ")
                .append("u.name, ")
                .append("u.email, ")
                .append("b.check_in, ")
                .append("b.check_out, ")
                .append("i.amount, ")
                .append("i.payment_status, ")
                .append("b.status ")
                .append("FROM bookings AS b ")
                .append("JOIN users AS u ON b.user_id = u.user_id ")
                .append("JOIN rooms AS r ON b.room_id = r.room_id ")
                .append("JOIN invoices AS i ON b.booking_id = i.booking_id ")
                .append("ORDER BY b.check_in DESC");

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapBooking(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error occurred while fetching all bookings", e);
        }
        return bookings;
    }

    private Booking mapBooking(ResultSet rs) throws SQLException {
        int bookingId = rs.getInt("booking_id");
        int userId = rs.getInt("user_id");
        int roomId = rs.getInt("room_id");
        LocalDateTime checkIn = rs.getTimestamp("check_in").toLocalDateTime();
        LocalDateTime checkOut = rs.getTimestamp("check_out").toLocalDateTime();
        BookingStatus status = BookingStatus.valueOf(rs.getString("status"));

        return new Booking(bookingId, userId, roomId, checkIn, checkOut, status);
    }

    private void saveBooking(Booking booking, boolean isNewBooking) throws SQLException {
        String sql;
        if (isNewBooking) {
            sql = "INSERT INTO bookings (user_id, room_id, check_in, check_out, status) VALUES (?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE bookings SET user_id = ?, room_id = ?, check_in = ?, check_out = ?, status = ? WHERE booking_id = ?";
        }

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, booking.getUserId());
            stmt.setInt(2, booking.getRoomId());
            stmt.setTimestamp(3, Timestamp.valueOf(booking.getCheckIn()));
            stmt.setTimestamp(4, Timestamp.valueOf(booking.getCheckOut()));
            stmt.setObject(5, booking.getStatus().name(), Types.OTHER);

            if (!isNewBooking) {
                stmt.setInt(6, booking.getBookingId());
            }

            stmt.executeUpdate();

            if (isNewBooking) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        booking.setBookingId(generatedKeys.getInt(1));
                    }
                }
            }
        }
    }
}
