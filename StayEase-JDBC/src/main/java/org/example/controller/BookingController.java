package org.example.controller;


import org.example.entity.Booking;
import org.example.exception.ServiceException;
import org.example.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class BookingController {

    private final BookingService bookingService;
    private static final Logger log = LoggerFactory.getLogger(BookingController.class);

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public void createBooking(Booking booking) {
        bookingService.createBooking(booking);
    }

    public void updateBooking(Booking booking) {
        bookingService.updateBooking(booking);
    }

    public boolean cancelBooking(int bookingId) {
        return bookingService.cancelBooking(bookingId);
    }

    public Booking getBookingById(int bookingId) {
        return bookingService.getBookingById(bookingId);
    }

    public List<Booking> getBookingsByUser(int userId) {
        return bookingService.getBookingsByUser(userId);
    }

    public Booking getConfirmedBookingByUserId(int loggedInGuest) {
        return bookingService.getConfirmedBookingByUserId(loggedInGuest);
    }

    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }
}
