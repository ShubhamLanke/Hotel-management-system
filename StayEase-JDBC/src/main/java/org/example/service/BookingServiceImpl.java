package org.example.service;

import org.example.dao.BookingDao;
import org.example.entity.Booking;

import java.util.List;

public class BookingServiceImpl implements BookingService {
    private final BookingDao bookingDao;

    public BookingServiceImpl(BookingDao bookingDao) {
        this.bookingDao = bookingDao;
    }

    @Override
    public void createBooking(Booking booking) {
        bookingDao.createBooking(booking);
    }

    @Override
    public void updateBooking(Booking booking) {
        bookingDao.updateBooking(booking);
    }

    @Override
    public void cancelBooking(int bookingId) {
        bookingDao.cancelBooking(bookingId);
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
