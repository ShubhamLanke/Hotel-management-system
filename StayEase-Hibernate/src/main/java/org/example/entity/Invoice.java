package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.constants.PaymentStatus;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Override
    public String toString() {
        return String.format("""
            ---------- INVOICE ----------
            Invoice ID     : %d
            Booking ID     : %d
            User ID        : %d
            Amount         : Rs.%.2f
            Issue Date     : %s
            Payment Status : %s
            ------------------------------
            """,
                invoiceId,
                bookingId,
                userId,
                amount,
                issueDate,
                paymentStatus
        );
    }
}
