package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.constants.BookingStatus;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Entity
@Table(name = "bookings")
public class Booking  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Integer bookingId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "check_in")
    private LocalDateTime checkIn;

    @Column(name = "check_out")
    private LocalDateTime checkOut;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    public Booking() {
    }

    public Booking(User user, Room room, LocalDateTime checkIn, LocalDateTime checkOut, BookingStatus status) {
        this.user = user;
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(bookingId, booking.bookingId);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "user=" + user +
                ", room=" + room +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", status=" + status +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(bookingId);
    }

//    @Override
//    public String toString() {
//        return "Booking{" +
//                "bookingId=" + bookingId +
//                ", user=" + (user != null ? user.getUserID() : null) +
//                ", room=" + (room != null ? room.getRoomID() : null) +
//                ", checkIn=" + checkIn +
//                ", checkOut=" + checkOut +
//                ", status=" + status +
//                '}';
//    }
}
