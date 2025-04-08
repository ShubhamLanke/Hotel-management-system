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

    public Response createBooking(Booking booking) {
        bookingService.createBooking(booking);
        return new Response(booking, ResponseStatus.SUCCESS, "Booking created successfully!");
    }

    public Response updateBooking(Booking booking) {
        bookingService.updateBooking(booking);
        return new Response(booking, ResponseStatus.SUCCESS, "Booking updated successfully!");
    }

    public Response cancelBooking(int bookingId) {
        boolean isCancelled = bookingService.cancelBooking(bookingId);
        if (isCancelled) {
            return new Response(null, ResponseStatus.SUCCESS, "Booking cancelled successfully!");
        } else {
            return new Response(null, ResponseStatus.ERROR, "Failed to cancel booking. Booking ID may not exist.");
        }
    }

    public Response getBookingById(int bookingId) {
        return bookingService.getBookingById(bookingId)
                .map(booking -> new Response(booking, ResponseStatus.SUCCESS, "Booking found!"))
                .orElse(new Response(null, ResponseStatus.ERROR, "Booking not found!"));
    }

    public Response getBookingsByUser(int userId) {
        List<Booking> bookings = bookingService.getBookingsByUser(userId);
        if (!bookings.isEmpty()) {
            return new Response(bookings, ResponseStatus.SUCCESS, "Bookings found");
        } else {
            return new Response(null, ResponseStatus.ERROR, "No bookings found");
        }
    }

    public Response getConfirmedBookingByUserId(int loggedInGuest) {
        return bookingService.getConfirmedBookingByUserId(loggedInGuest)
                .map(booking -> new Response(booking, ResponseStatus.SUCCESS, "Confirmed booking found!"))
                .orElse(new Response(null, ResponseStatus.ERROR, "No confirmed booking found!"));
    }

    public Response getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        if (!bookings.isEmpty()) {
            return new Response(bookings, ResponseStatus.SUCCESS, "All bookings retrieved successfully!");
        } else {
            return new Response(null, ResponseStatus.ERROR, "No bookings available.");
        }
    }
}
