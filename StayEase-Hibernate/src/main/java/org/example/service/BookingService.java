package org.example.service;

import org.example.entity.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    void createBooking(Booking booking);

    void updateBooking(Booking booking);

    boolean cancelBooking(int bookingId);

    Optional<Booking> getBookingById(int bookingId);

    List<Booking> getBookingsByUser(int userId);

    Optional<Booking> getConfirmedBookingByUserId(int userId);

    List<Booking> getAllBookings();

    List<Booking> getAllConfirmedBookingsByUserId(int userId);
}
