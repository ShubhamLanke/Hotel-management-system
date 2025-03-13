package org.example.service;

import org.example.entity.Booking;

import java.util.List;

public interface BookingService {
    void createBooking(Booking booking);
    void updateBooking(Booking booking);
    void cancelBooking(int bookingId);
    Booking getBookingById(int bookingId);
    List<Booking> getBookingsByUser(int userId);
    Booking getConfirmedBookingByUserId(int userId);

    List<Booking> getAllBookings();
}
