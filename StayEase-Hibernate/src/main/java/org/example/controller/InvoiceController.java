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
    Response response;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public Response generateInvoice(Invoice invoice) {
        int invoiceId = invoiceService.generateInvoice(invoice);
        return response = new Response(invoiceId, ResponseStatus.SUCCESS, "Invoice generated successfully with ID: " + invoiceId);
    }

    public Response getInvoiceByBookingId(int bookingId) {
        return response = invoiceService.getInvoiceByBookingId(bookingId)
                .map(invoice -> new Response(invoice, ResponseStatus.SUCCESS, "Invoice found successfully."))
                .orElse(new Response(null, ResponseStatus.ERROR, "No invoice found for booking ID: " + bookingId));
    }

    public Response updatePaymentStatus(int invoiceId, PaymentStatus status) {
        boolean isUpdated = invoiceService.updatePaymentStatus(invoiceId, status);
        if (isUpdated) {
            return response = new Response(null, ResponseStatus.SUCCESS, "Payment status updated successfully for Invoice ID: " + invoiceId);
        } else {
            return response = new Response(null, ResponseStatus.ERROR, "Failed to update payment status. Invoice ID may not exist.");
        }
    }

    public Response getInvoiceById(int invoiceId) {
        return response = invoiceService.getInvoiceById(invoiceId)
                .map(invoice -> new Response(invoice, ResponseStatus.SUCCESS, "Invoice found successfully."))
                .orElse(new Response(null, ResponseStatus.ERROR, "No invoice found for ID: " + invoiceId));
    }

    public Response getInvoiceByUserId(int userID) {
        List<Invoice> invoices = invoiceService.getInvoiceByUserId(userID);
        if (!invoices.isEmpty()) {
            return response = new Response(invoices, ResponseStatus.SUCCESS, "Invoices found for user ID: " + userID);
        } else {
            return response = new Response(null, ResponseStatus.ERROR, "No invoices found for user ID: " + userID);
        }
    }
}
