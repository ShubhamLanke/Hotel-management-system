package org.example.dao;

import org.example.constants.PaymentStatus;
import org.example.entity.Booking;
import org.example.entity.Invoice;
import org.example.entity.User;

import java.util.List;
import java.util.Optional;

public interface InvoiceDao {


    int generateInvoice(Invoice invoice);

    Optional<Invoice> getInvoiceByBooking(Booking booking);

    List<Invoice> getAllInvoices();

    void updatePaymentStatus(int invoiceId, PaymentStatus status);

    Optional<Invoice> getInvoiceById(int invoiceId);

    List<Invoice> getInvoiceByUserId(User user);

    boolean updateInvoice(Invoice invoice);
}
