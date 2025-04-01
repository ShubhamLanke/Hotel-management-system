package org.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.example.constants.PaymentStatus;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
public class Invoice {
    @Id
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
