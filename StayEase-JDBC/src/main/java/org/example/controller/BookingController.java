package org.example.controller;

import org.example.constants.ResponseStatus;
import org.example.entity.Booking;
import org.example.service.BookingService;
import org.example.utility.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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

    public Response getBookingById(int bookingId) {
        return bookingService.getBookingById(bookingId)
                .map(booking -> new Response(booking, ResponseStatus.SUCCESS, "Booking found!"))
                .orElse(new Response(null, ResponseStatus.ERROR,"Booking not found!"));
    }


    public Response getBookingsByUser(int userId) {
        return bookingService.getBookingsByUser(userId);
    }

    public Response getConfirmedBookingByUserId(int loggedInGuest) {
        return bookingService.getConfirmedBookingByUserId(loggedInGuest)
                .map(booking -> new Response(booking, ResponseStatus.SUCCESS, "Confirmed booking found!"))
                .orElse(new Response(null, ResponseStatus.ERROR, "Booking not found!"));
    }

    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }
}
