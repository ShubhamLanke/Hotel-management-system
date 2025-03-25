package org.example.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.constants.RoomType;

@Getter
@Setter
public class Room {

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






