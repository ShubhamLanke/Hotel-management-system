package org.example.entity;

import lombok.Getter;
import lombok.Setter;
import org.example.constants.BookingStatus;

import java.time.LocalDateTime;

@Setter
@Getter
public class Booking {
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
