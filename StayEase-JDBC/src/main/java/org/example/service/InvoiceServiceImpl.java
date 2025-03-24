package org.example.service;

import org.example.constants.PaymentStatus;
import org.example.dao.InvoiceDao;
import org.example.entity.Invoice;
import org.example.exception.ServiceException;

import java.util.List;
import java.util.Optional;

public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceDao invoiceDao;

    public InvoiceServiceImpl(InvoiceDao invoiceDao) {
        this.invoiceDao = invoiceDao;
    }

    @Override
    public int generateInvoice(Invoice invoice) {
        if (invoice == null) {
            throw new ServiceException("Invoice cannot be null.");
        }
        if (invoice.getAmount() <= 0) {
            throw new ServiceException("Invalid invoice amount.");
        }
        return invoiceDao.generateInvoice(invoice);
    }

    @Override
    public Optional<Invoice> getInvoiceByBookingId(int bookingId) {
        if (bookingId <= 0) {
            throw new ServiceException("Invalid booking ID.");
        }
        Invoice invoice = invoiceDao.getInvoiceByBookingId(bookingId);
        return Optional.ofNullable(invoice); // Return Optional.empty() if invoice is not found
    }

    @Override
    public List<Invoice> getAllInvoices() {
        return invoiceDao.getAllInvoices();
    }

    @Override
    public void updatePaymentStatus(int invoiceId, PaymentStatus status) {
        if (invoiceId <= 0) {
            throw new ServiceException("Invalid invoice ID.");
        }
        if (status == null) {
            throw new ServiceException("Payment status cannot be null.");
        }
        invoiceDao.updatePaymentStatus(invoiceId, status);
    }

    @Override
    public Optional<Invoice> getInvoiceById(int invoiceId) {
        if (invoiceId <= 0) {
            throw new ServiceException("Invalid invoice ID.");
        }
        Invoice invoice = invoiceDao.getInvoiceById(invoiceId);
        return Optional.ofNullable(invoice); // Return Optional.empty() if invoice is not found
    }

    @Override
    public List<Invoice> getInvoiceByUserId(int userID) {
        if (userID <= 0) {
            throw new ServiceException("Invalid user ID.");
        }
        return invoiceDao.getInvoiceByUserId(userID);
    }
}
