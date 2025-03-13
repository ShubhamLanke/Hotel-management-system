package org.example.dao;

import org.example.constants.PaymentStatus;
import org.example.entity.Invoice;

import java.util.List;

public interface InvoiceDao {
    int generateInvoice(Invoice invoice);

    Invoice getInvoiceByBookingId(int invoiceId);

    List<Invoice> getAllInvoices();

    void updatePaymentStatus(int invoiceId, PaymentStatus status);

    Invoice getInvoiceById(int invoiceId);

    List<Invoice> getInvoiceByUserId(int userID);
}
