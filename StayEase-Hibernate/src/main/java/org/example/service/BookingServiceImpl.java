package org.example.service;

import lombok.extern.log4j.Log4j2;
import org.example.dao.BookingDao;
import org.example.entity.Booking;
import org.example.entity.User;
import org.example.exception.BookingNotFoundException;
import org.example.exception.ServiceException;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Log4j2
public class BookingServiceImpl implements BookingService {
    private final BookingDao bookingDao;
    private final UserService userService;


    public BookingServiceImpl(BookingDao bookingDao, UserService userService) {
        this.bookingDao = bookingDao;
        this.userService = userService;
    }

    @Override
    public void createBooking(Booking booking) {
        if (Objects.isNull(booking)) {
            throw new ServiceException("INVALID_BOOKING_DETAILS_PROVIDED");

        }
        bookingDao.createBooking(booking);
        log.info("Booking created successfully with ID: {}", booking.getBookingId());
    }

    @Override
    public void updateBooking(Booking booking) {
        if (Objects.isNull(booking)) {
            throw new ServiceException("INVALID_BOOKING_DETAILS_PROVIDED");
        }
        bookingDao.updateBooking(booking);
        log.info("Booking updated successfully with ID: {}", booking.getBookingId());
    }

    @Override
    public boolean cancelBooking(int bookingId) {
        Booking booking = bookingDao.getBookingById(bookingId);
        try {
            if(Objects.isNull(booking)) {
                throw new BookingNotFoundException("Booking not found with ID: " + bookingId);
            }
            bookingDao.cancelBooking(bookingId);
            return true;
        } catch (Exception e) {
            log.error("Unexpected error while canceling booking ID {}: {}", bookingId, e);
            return false;
        }
    }

    @Override
    public Optional<Booking> getBookingById(int bookingId) {
        return Optional.ofNullable(bookingDao.getBookingById(bookingId));
    }


    @Override
    public List<Booking> getBookingsByUser(int userId) {
        Optional<User> user = userService.getUserById(userId);
        List<Booking> booking = bookingDao.getBookingsByUser(user.get());
        if(booking == null){
            throw new ServiceException("No bookings found for" + user.get().getName());
        }
        return booking;
    }

    @Override
    public Optional<Booking> getConfirmedBookingByUserId(int userId) {
        return userService.getUserById(userId)
                .flatMap(bookingDao::getConfirmedBookingByUserId);
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingDao.getAllBookings();
    }

    @Override
    public List<Booking> getAllConfirmedBookingsByUserId(int userId) {
        return userService.getUserById(userId)
                .map(bookingDao::getAllConfirmedBookingByUser)
                .orElse(Collections.emptyList());
    }
}
