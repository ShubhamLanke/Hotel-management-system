package org.example.entity;

import org.example.constants.PaymentStatus;

import java.time.LocalDateTime;

public class Invoice {
    private Integer invoiceId;
    private Integer bookingId;
    private Integer userId;
    private double amount;
    private LocalDateTime issueDate;
    private PaymentStatus paymentStatus;

    public Invoice(Integer invoiceId, Integer bookingId, Integer userId, double amount, LocalDateTime issueDate, PaymentStatus paymentStatus) {
        this.invoiceId = invoiceId;
        this.bookingId = bookingId;
        this.userId = userId;
        this.amount = amount;
        this.issueDate = issueDate;
        this.paymentStatus = paymentStatus;
    }

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
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
