package org.example.dao;

import jakarta.persistence.PersistenceException;
import jakarta.persistence.RollbackException;
import lombok.extern.log4j.Log4j2;
import org.example.constants.BookingStatus;
import org.example.entity.Booking;
import org.example.entity.User;
import org.example.persistence.PersistenceManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Log4j2
public class BookingDaoImpl implements BookingDao {

    private  final EntityManager entityManager;

    public BookingDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void createBooking(Booking booking) {
        try {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                entityManager.persist(booking);
                transaction.commit();
            } catch (IllegalStateException | RollbackException ex) {
                try {
                    transaction.rollback();
                } catch (IllegalStateException | PersistenceException pe) {
                    log.error("Error occured while rolling back: ", pe);
                }
//                log.error("Error while creating booking for user ID: {}", booking.getUser().getUserID(), ex);
            }
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void updateBooking(Booking booking) {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                entityManager.merge(booking);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                log.error("Error while updating booking ID: " + booking.getBookingId(), e);
            }
        }
    }

    @Override
    public void cancelBooking(int bookingId) {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                Booking booking = entityManager.find(Booking.class, bookingId);
                if (booking != null) {
                    booking.setStatus(BookingStatus.CANCELLED);
                    entityManager.merge(booking);
                }
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                log.error("Error while canceling booking ID: " + bookingId, e);
            }
        }
    }

    @Override
    public Booking getBookingById(int bookingId) {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            return entityManager.find(Booking.class, bookingId);
        } catch (Exception e) {
            log.error("Error fetching booking by ID: {}", bookingId, e);
            return null;
        }
    }

    @Override
    public List<Booking> getBookingsByUser(User user) {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            return entityManager.createQuery(
                            "SELECT b FROM Booking b WHERE b.user = :user ORDER BY b.checkIn DESC LIMIT 3",
                            Booking.class)
                    .setParameter("user", user)
                    .setMaxResults(3)
                    .getResultList();
        } catch (Exception e) {
            log.error("Error fetching bookings for user ID: " + user.getUserID(), e);
        }
        return List.of();
    }

    @Override
    public Optional<Booking> getConfirmedBookingByUserId(User user) {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            Booking booking = entityManager.createQuery(
                            "SELECT b FROM Booking b WHERE b.user = :user AND b.status = :status",
                            Booking.class)
                    .setParameter("user", user)
                    .setParameter("status", BookingStatus.CONFIRMED)
                    .getSingleResult();
            return Optional.ofNullable(booking);
        } catch (Exception e) {
            log.error("Error occurred while fetching confirmed booking for user ID: " + user.getUserID(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Booking> getAllBookings() {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            return entityManager.createQuery(
                            " SELECT b FROM Booking b JOIN FETCH b.user JOIN FETCH b.room ORDER BY b.checkIn DESC ",
                            Booking.class)
                    .getResultList();
        } catch (Exception e) {
            log.error("Error occurred while fetching all bookings", e);
        }
        return List.of();
    }

    @Override
    public List<Booking> getAllConfirmedBookingByUser(User user) {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            return entityManager.createQuery(
                            "SELECT b FROM Booking b WHERE b.user = :user AND b.status = :status",
                            Booking.class)
                    .setParameter("user", user)
                    .setParameter("status", BookingStatus.CONFIRMED)
                    .getResultList();
        } catch (Exception e) {
            log.error("Error occurred while fetching confirmed bookings for user ID: " + user.getUserID(), e);
            return Collections.emptyList();
        }
    }

}
