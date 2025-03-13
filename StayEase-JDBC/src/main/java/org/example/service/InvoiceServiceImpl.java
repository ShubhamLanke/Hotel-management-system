package org.example.service;

import org.example.constants.PaymentStatus;
import org.example.dao.InvoiceDao;
import org.example.entity.Invoice;

import java.util.List;

public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceDao invoiceDao;

    public InvoiceServiceImpl(InvoiceDao invoiceDao) {
        this.invoiceDao = invoiceDao;
    }

    @Override
    public int generateInvoice(Invoice invoice) {
        int invoiceId = invoiceDao.generateInvoice(invoice);
        return invoiceId;
    }

    @Override
    public Invoice getInvoiceByBookingId(int bookingId) {
        return invoiceDao.getInvoiceByBookingId(bookingId);
    }

    @Override
    public List<Invoice> getAllInvoices() {
        return invoiceDao.getAllInvoices();
    }

    @Override
    public void updatePaymentStatus(int invoiceId, PaymentStatus status) {
        invoiceDao.updatePaymentStatus(invoiceId, status);
    }

    @Override
    public Invoice getInvoiceById(int invoiceId) {
        return invoiceDao.getInvoiceById(invoiceId);
    }

    @Override
    public List<Invoice> getInvoiceByUserId(int userID) {
        return invoiceDao.getInvoiceByUserId(userID);
    }
}
