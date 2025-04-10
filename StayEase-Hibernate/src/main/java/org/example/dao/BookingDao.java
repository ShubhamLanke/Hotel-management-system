package org.example.dao;

import org.example.entity.Booking;
import org.example.entity.User;

import java.util.List;
import java.util.Optional;

public interface BookingDao {
    void createBooking(Booking booking);

    void updateBooking(Booking booking);

    void cancelBooking(int bookingId);

    Optional<Booking> getBookingById(int bookingId);

    List<Booking> getBookingsByUser(User user);

    Optional<Booking> getConfirmedBookingByUserId(int userId);  // Use Optional to handle absence of confirmed bookings

    List<Booking> getAllBookings();
}
