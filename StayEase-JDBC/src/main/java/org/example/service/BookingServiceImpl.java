package org.example.service;

import org.example.dao.BookingDao;
import org.example.entity.Booking;
import org.example.exception.BookingNotFoundException;
import org.example.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class BookingServiceImpl implements BookingService {
    private final BookingDao bookingDao;
    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);


    public BookingServiceImpl(BookingDao bookingDao) {
        this.bookingDao = bookingDao;
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
        try {
            if (bookingDao.getBookingById(bookingId).isEmpty()) {
                throw new BookingNotFoundException("Booking not found with ID: " + bookingId);
            }
            bookingDao.cancelBooking(bookingId);
            return true;
        } catch (BookingNotFoundException e) {
            log.error("Booking cancellation failed for ID {}: {}", bookingId, e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Unexpected error while canceling booking ID {}: {}", bookingId, e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<Booking> getBookingById(int bookingId) {
        return Optional.ofNullable(bookingDao.getBookingById(bookingId))
                .orElseThrow(() -> new ServiceException("BOOKING_NOT_FOUND"));
    }

    @Override
    public List<Booking> getBookingsByUser(int userId) {
        return bookingDao.getBookingsByUser(userId);
    }

    @Override
    public Optional<Booking> getConfirmedBookingByUserId(int userId) {
        return bookingDao.getConfirmedBookingByUserId(userId);
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingDao.getAllBookings();
    }
}
