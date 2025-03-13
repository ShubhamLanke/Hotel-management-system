package org.example.entity;

import org.example.constants.PaymentStatus;

import java.time.LocalDateTime;

public class Invoice {
    private int invoiceId;
    private int bookingId;
    private int userId;
    private double amount;
    private LocalDateTime issueDate;
    private PaymentStatus paymentStatus;

    public Invoice(int invoiceId, int bookingId, int userId, double amount, LocalDateTime issueDate, PaymentStatus paymentStatus) {
        this.invoiceId = invoiceId;
        this.bookingId = bookingId;
        this.userId = userId;
        this.amount = amount;
        this.issueDate = issueDate;
        this.paymentStatus = paymentStatus;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDateTime issueDate) {
        this.issueDate = issueDate;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @Override
    public String toString() {
        return String.format(
                "\n---------- INVOICE ----------\n" +
                        "Invoice ID     : %d\n" +
                        "Booking ID     : %d\n" +
                        "User ID        : %d\n" +
                        "Amount         : Rs.%.2f\n" +
                        "Issue Date     : %s\n" +
                        "Payment Status : %s\n" +
                        "------------------------------\n",
                invoiceId,
                bookingId,
                userId,
                amount,
                issueDate,
                paymentStatus
        );
    }
}
