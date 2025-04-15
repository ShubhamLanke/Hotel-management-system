package org.example.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.example.constants.PaymentStatus;
import org.example.entity.Booking;
import org.example.entity.Invoice;
import org.example.entity.User;
import org.example.persistence.PersistenceManager;

import java.util.List;
import java.util.Optional;

public class InvoiceDaoImpl implements InvoiceDao {

    @Override
    @Transactional
    public int generateInvoice(Invoice invoice) {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            entityManager.getTransaction().begin();
            entityManager.persist(invoice);
            entityManager.getTransaction().commit();
            return invoice.getInvoiceId();
        } catch (Exception e) {
            throw new RuntimeException("Error generating invoice", e);
        }
    }

    @Override
    public Optional<Invoice> getInvoiceByBooking(Booking booking) {

        Booking booking1 = new Booking();
        booking1.setBookingId(booking.getBookingId());
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            TypedQuery<Invoice> query = entityManager.createQuery(
                    "SELECT i FROM Invoice i WHERE i.booking = :booking", Invoice.class);
            query.setParameter("booking", booking1);

            List<Invoice> resultList = query.getResultList();
            return resultList.stream().findFirst();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching invoice by booking ID: " + booking, e);
        }
    }

    @Override
    public Optional<Invoice> getInvoiceById(int invoiceId) {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            Invoice invoice = entityManager.find(Invoice.class, invoiceId);
            return Optional.ofNullable(invoice);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching invoice by ID: " + invoiceId, e);
        }
    }

    @Override
    public List<Invoice> getAllInvoices() {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            TypedQuery<Invoice> query = entityManager.createQuery("SELECT i FROM Invoice i", Invoice.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching all invoices", e);
        }
    }

    @Override
    @Transactional
    public void updatePaymentStatus(int invoiceId, PaymentStatus status) {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            entityManager.getTransaction().begin();
            Invoice invoice = entityManager.find(Invoice.class, invoiceId);
            if (invoice != null) {
                invoice.setPaymentStatus(status);
                entityManager.merge(invoice);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Error updating payment status for invoice ID: " + invoiceId, e);
        }
    }

    @Override
    public List<Invoice> getInvoiceByUserId(User user) {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            TypedQuery<Invoice> query = entityManager.createQuery(
                    "SELECT i FROM Invoice i WHERE i.user = :user", Invoice.class);
            query.setParameter("user", user);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching invoices for user ID: " + user.getUserID(), e);
        }
    }
}
