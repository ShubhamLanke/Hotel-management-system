package org.example.dao;

import lombok.extern.log4j.Log4j2;
import org.example.constants.BookingStatus;
import org.example.entity.Booking;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log4j2
@Transactional
public class BookingDaoImpl implements BookingDao {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public void createBooking(Booking booking) {
        try {
            entityManager.persist(booking);
        } catch (Exception e) {
            log.error("Error while creating booking for user ID: {}", booking.getUserId(), e);
        }
    }


    @Override
    public void updateBooking(Booking booking) {
        try {
            entityManager.merge(booking);
        } catch (Exception e) {
            log.error("Error while updating booking ID: " + booking.getBookingId(), e);
        }
    }

    @Override
    public void cancelBooking(int bookingId) {
        try {
            Booking booking = entityManager.find(Booking.class, bookingId);
            if (booking != null) {
                booking.setStatus(BookingStatus.CANCELLED);
                entityManager.merge(booking);
            }
        } catch (Exception e) {
            log.error("Error while canceling booking ID: " + bookingId, e);
        }
    }

    @Override
    public Optional<Booking> getBookingById(int bookingId) {
        try {
            Booking booking = entityManager.find(Booking.class, bookingId);
            return Optional.ofNullable(booking);
        } catch (Exception e) {
            log.error("Error fetching booking by ID: " + bookingId, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Booking> getBookingsByUser(int userId) {
        try {
            return entityManager.createQuery("SELECT b FROM Booking b WHERE b.userId = :userId ORDER BY b.checkIn DESC", Booking.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } catch (Exception e) {
            log.error("Error fetching bookings for user ID: " + userId, e);
        }
        return List.of();
    }

    @Override
    public Optional<Booking> getConfirmedBookingByUserId(int userId) {
        try {
            Booking booking = entityManager.createQuery("SELECT b FROM Booking b WHERE b.userId = :userId AND b.status = :status", Booking.class)
                    .setParameter("userId", userId)
                    .setParameter("status", BookingStatus.CONFIRMED)
                    .getSingleResult();
            return Optional.ofNullable(booking);
        } catch (Exception e) {
            log.error("Error occurred while fetching confirmed booking for user ID: " + userId, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Booking> getAllBookings() {
        try {
            return entityManager.createQuery("SELECT b FROM Booking b JOIN FETCH b.user JOIN FETCH b.room JOIN FETCH b.invoice ORDER BY b.checkIn DESC", Booking.class)
                    .getResultList();
        } catch (Exception e) {
            log.error("Error occurred while fetching all bookings", e);
        }
        return List.of();
    }
}
