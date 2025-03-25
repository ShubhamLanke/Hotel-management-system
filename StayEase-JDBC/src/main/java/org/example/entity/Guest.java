package org.example.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Guest {
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
