package org.example.dao;

import org.example.entity.Booking;
import org.example.entity.User;

import java.util.List;
import java.util.Optional;

public interface BookingDao {
    void createBooking(Booking booking);

    void updateBooking(Booking booking);

    void cancelBooking(int bookingId);

    Booking getBookingById(int bookingId);

    List<Booking> getBookingsByUser(User user);

    Optional<Booking> getConfirmedBookingByUserId(User user);

    List<Booking> getAllBookings();

    List<Booking> getAllConfirmedBookingByUser(User user);
}
