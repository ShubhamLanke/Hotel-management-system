package org.example.service;

import org.example.constants.PaymentStatus;
import org.example.entity.Invoice;

import java.util.List;
import java.util.Optional;

public interface InvoiceService {
    int generateInvoice(Invoice invoice);

    Optional<Invoice> getInvoiceByBookingId(int bookingId);

    List<Invoice> getAllInvoices();

    boolean updatePaymentStatus(int invoiceId, PaymentStatus status);

    Optional<Invoice> getInvoiceById(int invoiceId);

    List<Invoice> getInvoiceByUserId(int userID);
}