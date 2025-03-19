package org.example.service;

import org.example.controller.BookingController;
import org.example.dao.BookingDao;
import org.example.entity.Booking;
import org.example.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class BookingServiceImpl implements BookingService {
    private final BookingDao bookingDao;
    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);


    public BookingServiceImpl(BookingDao bookingDao) {
        this.bookingDao = bookingDao;
    }

    @Override
    public void createBooking(Booking booking) {
        if(Objects.isNull(booking)){
            throw new ServiceException("INVALID_BOOKING_DETAILS_PROVIDED");
        }
        bookingDao.createBooking(booking);
    }

    @Override
    public void updateBooking(Booking booking) {
        if(Objects.isNull(booking)){
            throw new ServiceException("INVALID_BOOKING_DETAILS_PROVIDED");
        }
        bookingDao.updateBooking(booking);
    }

    @Override
    public boolean cancelBooking(int bookingId) {
        try {
            bookingDao.cancelBooking(bookingId);
            return true;
        } catch (Exception e) {
            log.error("Unexpected error while canceling booking ID {}: {}", bookingId, e.getMessage());
            return false;
        }
    }

    @Override
    public Booking getBookingById(int bookingId) {
        return bookingDao.getBookingById(bookingId);
    }

    @Override
    public List<Booking> getBookingsByUser(int userId) {
        return bookingDao.getBookingsByUser(userId);
    }

    @Override
    public Booking getConfirmedBookingByUserId(int userId) {
        return bookingDao.getConfirmedBookingByUserId(userId);
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingDao.getAllBookings();
    }
}
