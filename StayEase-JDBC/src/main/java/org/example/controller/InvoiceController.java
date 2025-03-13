package org.example.controller;

import org.example.constants.PaymentStatus;
import org.example.entity.Invoice;
import org.example.service.InvoiceService;

import java.util.List;

public class InvoiceController {
    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public int generateInvoice(Invoice invoice) {
        return invoiceService.generateInvoice(invoice);  // Get generated ID
    }

    public Invoice getInvoiceByBookingId(int bookingId) {
        return invoiceService.getInvoiceByBookingId(bookingId);
    }

    public void updatePaymentStatus(int invoiceId, PaymentStatus status) { // TODO response model implement
        invoiceService.updatePaymentStatus(invoiceId, status);
        System.out.println("Payment status updated!");
    }

    public Invoice getInvoiceById(int invoiceId) {
        return invoiceService.getInvoiceById(invoiceId);
    }

    public List<Invoice> getInvoiceByUserId(int userID) {
        return invoiceService.getInvoiceByUserId(userID);
    }
}
