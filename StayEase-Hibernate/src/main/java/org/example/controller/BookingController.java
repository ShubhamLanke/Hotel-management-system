package org.example.controller;

import lombok.extern.log4j.Log4j2;
import org.example.constants.ResponseStatus;
import org.example.entity.Booking;
import org.example.service.BookingService;
import org.example.utility.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.print.Book;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Log4j2
public class BookingController {

    private final BookingService bookingService;

    Response response;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public Response createBooking(Booking booking) {
        if (Objects.isNull(booking)) {
            return response = new Response(null, ResponseStatus.ERROR, "Booking details are missing or invalid.");
        }

        try {
            bookingService.createBooking(booking);
            return response = new Response(booking, ResponseStatus.SUCCESS, "Booking created successfully!");
        } catch (Exception e) {
            log.error("Failed to create booking.",e);
            return response = new Response(null, ResponseStatus.ERROR, "Failed to create booking. Please try again or contact support.");
        }
    }

    public Response updateBooking(Booking booking) {
        bookingService.updateBooking(booking);
        return response = new Response(booking, ResponseStatus.SUCCESS, "Booking updated successfully!");
    }
    public Response cancelBooking(int bookingId) {
        Optional<Booking> optionalBooking = bookingService.getBookingById(bookingId);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            boolean isCancelled = bookingService.cancelBooking(booking.getBookingId());
            if (isCancelled) {
                return response = new Response(booking, ResponseStatus.SUCCESS, "Booking cancelled successfully!");
            }
        }
        return response = new Response(null, ResponseStatus.ERROR, "Failed to cancel booking. Booking ID may not exist.");
    }

    public Response getBookingById(int bookingId) {
        return response = bookingService.getBookingById(bookingId)
                .map(booking -> new Response(booking, ResponseStatus.SUCCESS, "Booking found!"))
                .orElse(new Response(null, ResponseStatus.ERROR, "Booking not found!"));
    }

    public Response getBookingsByUser(int userId) {
        List<Booking> bookings = bookingService.getBookingsByUser(userId);
        if (!bookings.isEmpty()) {
            return response = new Response(bookings, ResponseStatus.SUCCESS, "Bookings found");
        } else {
            return response = new Response(null, ResponseStatus.ERROR, "No bookings found");
        }
    }

    public Response getConfirmedBookingByUserId(int loggedInGuest) {
        return response = bookingService.getConfirmedBookingByUserId(loggedInGuest)
                .map(booking -> new Response(booking, ResponseStatus.SUCCESS, "Confirmed booking found!"))
                .orElse(new Response(null, ResponseStatus.ERROR, "No confirmed booking found!"));
    }

    public Response getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        if (!bookings.isEmpty()) {
            return response = new Response(bookings, ResponseStatus.SUCCESS, "All bookings retrieved successfully!");
        } else {
            return response = new Response(null, ResponseStatus.ERROR, "No bookings available.");
        }
    }

    public Response getAllConfirmedBookingsByUserId(int userId) {
        List<Booking> confirmedBookings = bookingService.getAllConfirmedBookingsByUserId(userId);

        if (confirmedBookings.isEmpty()) {
            return new Response(null, ResponseStatus.ERROR, "No confirmed bookings found!");
        } else {
            return new Response(confirmedBookings, ResponseStatus.SUCCESS, "Confirmed bookings retrieved successfully.");
        }
    }

}
