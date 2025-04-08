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
    private Integer roomID;
    private int roomNumber;
    private RoomType roomType;
    private double price;
    private boolean isAvailable;

    public Room() {
    }

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






