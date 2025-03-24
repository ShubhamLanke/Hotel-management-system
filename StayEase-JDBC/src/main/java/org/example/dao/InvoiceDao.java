package org.example.dao;

import org.example.constants.PaymentStatus;
import org.example.entity.Invoice;

import java.util.List;
import java.util.Optional;

public interface InvoiceDao {

    /**
     * Generates an invoice and returns the generated invoice ID.
     * @param invoice The invoice to be generated.
     * @return The ID of the newly generated invoice.
     */
    int generateInvoice(Invoice invoice);

    /**
     * Fetches the invoice associated with a given booking ID.
     * @param bookingId The booking ID whose invoice is to be fetched.
     * @return An Optional containing the Invoice if found, or an empty Optional if not.
     */
    Optional<Invoice> getInvoiceByBookingId(int bookingId);

    /**
     * Fetches all invoices from the database.
     * @return A list of all invoices.
     */
    List<Invoice> getAllInvoices();

    /**
     * Updates the payment status of an invoice.
     * @param invoiceId The invoice ID whose payment status needs to be updated.
     * @param status The new payment status to be set.
     */
    void updatePaymentStatus(int invoiceId, PaymentStatus status);

    /**
     * Fetches the invoice by its unique ID.
     * @param invoiceId The unique ID of the invoice.
     * @return An Optional containing the Invoice if found, or an empty Optional if not.
     */
    Optional<Invoice> getInvoiceById(int invoiceId);

    /**
     * Fetches all invoices for a given user ID.
     * @param userID The user ID whose invoices need to be fetched.
     * @return A list of invoices associated with the specified user.
     */
    List<Invoice> getInvoiceByUserId(int userID);
}
