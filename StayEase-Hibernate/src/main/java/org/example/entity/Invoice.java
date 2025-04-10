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
    @Column(name = "invoice_id")
    private Integer invoiceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private double amount;

    @Column(name = "issue_date")
    private LocalDateTime issueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    public Invoice() {
    }

    public Invoice(Integer invoiceId, Booking booking, User user, double amount, LocalDateTime issueDate, PaymentStatus paymentStatus) {
        this.invoiceId = invoiceId;
        this.booking = booking;
        this.user = user;
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
                booking != null ? booking.getBookingId() : null,
                user != null ? user.getUserID() : null,
                amount,
                issueDate,
                paymentStatus
        );
    }
}
