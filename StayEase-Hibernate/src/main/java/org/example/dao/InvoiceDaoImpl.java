package org.example.dao;

import org.example.constants.PaymentStatus;
import org.example.entity.Invoice;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;
import java.util.Optional;

public class InvoiceDaoImpl implements InvoiceDao {
    private final SessionFactory sessionFactory;

    public InvoiceDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public int generateInvoice(Invoice invoice) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            int id = (int) session.save(invoice);
            transaction.commit();
            return id;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public Optional<Invoice> getInvoiceByBookingId(int bookingId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Invoice> query = session.createQuery("FROM Invoice WHERE bookingId = :bookingId", Invoice.class);
            query.setParameter("bookingId", bookingId);
            return query.uniqueResultOptional();
        }
    }

    @Override
    public Optional<Invoice> getInvoiceById(int invoiceId) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(Invoice.class, invoiceId));
        }
    }

    @Override
    public List<Invoice> getAllInvoices() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Invoice", Invoice.class).list();
        }
    }

    @Override
    public void updatePaymentStatus(int invoiceId, PaymentStatus status) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Invoice invoice = session.get(Invoice.class, invoiceId);
            if (invoice != null) {
                invoice.setPaymentStatus(status);
                session.update(invoice);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public List<Invoice> getInvoiceByUserId(int userId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Invoice> query = session.createQuery("FROM Invoice WHERE userId = :userId", Invoice.class);
            query.setParameter("userId", userId);
            return query.list();
        }
    }
}
