package org.example.controller;

import org.example.constants.PaymentStatus;
import org.example.constants.ResponseStatus;
import org.example.entity.Invoice;
import org.example.service.InvoiceService;
import org.example.utility.Response;

import java.util.List;
import java.util.Optional;

public class InvoiceController {
    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public Response generateInvoice(Invoice invoice) {  // Now returns a Response instead of int
        int invoiceId = invoiceService.generateInvoice(invoice);
        return new Response(invoiceId, ResponseStatus.SUCCESS, "Invoice generated successfully with ID: " + invoiceId);
    }

    public Response getInvoiceByBookingId(int bookingId) {
        return invoiceService.getInvoiceByBookingId(bookingId)
                .map(invoice -> new Response(invoice, ResponseStatus.SUCCESS, "Invoice found successfully."))
                .orElse(new Response(null, ResponseStatus.ERROR, "No invoice found for booking ID: " + bookingId));
    }

    public Response updatePaymentStatus(int invoiceId, PaymentStatus status) {
        boolean isUpdated = invoiceService.updatePaymentStatus(invoiceId, status);
        if (isUpdated) {
            return new Response(null, ResponseStatus.SUCCESS, "Payment status updated successfully for Invoice ID: " + invoiceId);
        } else {
            return new Response(null, ResponseStatus.ERROR, "Failed to update payment status. Invoice ID may not exist.");
        }
    }


    public Response getInvoiceById(int invoiceId) { // Implemented response model
        return invoiceService.getInvoiceById(invoiceId)
                .map(invoice -> new Response(invoice, ResponseStatus.SUCCESS, "Invoice found successfully."))
                .orElse(new Response(null, ResponseStatus.ERROR, "No invoice found for ID: " + invoiceId));
    }

    public Response getInvoiceByUserId(int userID) { // Implemented response model
        List<Invoice> invoices = invoiceService.getInvoiceByUserId(userID);
        if (!invoices.isEmpty()) {
            return new Response(invoices, ResponseStatus.SUCCESS, "Invoices found for user ID: " + userID);
        } else {
            return new Response(null, ResponseStatus.ERROR, "No invoices found for user ID: " + userID);
        }
    }
}
