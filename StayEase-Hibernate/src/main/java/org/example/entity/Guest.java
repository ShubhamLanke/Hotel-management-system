package org.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer guestId;
    private String name;
    private int age;
    private Integer userId;

    public Guest(){}

    public Guest(Integer guestId, String name, int age, Integer userId) {
        this.guestId = guestId;
        this.name = name;
        this.age = age;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Guest{" +
                "guestId=" + guestId +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", userId=" + userId +
                '}';
    }
}
