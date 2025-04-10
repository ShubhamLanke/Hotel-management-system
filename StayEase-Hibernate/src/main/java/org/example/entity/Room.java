package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.constants.RoomType;

@Data
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Integer roomID;

    @Column(name = "room_number", nullable = false, unique = true)
    private int roomNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type",nullable = false)
    private RoomType roomType;

    @Column(nullable = false)
    private double price;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable;

    public Room() {}

    public Room(Integer roomID, int roomNumber, RoomType roomType, double price, boolean isAvailable) {
        this.roomID = roomID;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.price = price;
        this.isAvailable = isAvailable;
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomID=" + roomID +
                ", roomNumber=" + roomNumber +
                ", roomType=" + roomType +
                ", price=" + price +
                ", isAvailable=" + isAvailable +
                '}';
    }
}
