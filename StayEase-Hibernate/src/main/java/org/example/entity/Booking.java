package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.constants.BookingStatus;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bookings")   
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookingId;

    private Integer userId;

    private Integer roomId;

    private LocalDateTime checkIn;

    private LocalDateTime checkOut;

    private BookingStatus status;

    public Booking() {
    }

    public Booking(Integer bookingId, Integer userId, Integer roomId, LocalDateTime checkIn, LocalDateTime checkOut, BookingStatus status) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.roomId = roomId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId='" + bookingId + '\'' +
                ", userId='" + userId + '\'' +
                ", roomId='" + roomId + '\'' +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", status=" + status +
                '}';
    }
}
