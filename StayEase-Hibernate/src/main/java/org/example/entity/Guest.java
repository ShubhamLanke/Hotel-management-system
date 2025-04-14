package org.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "guests")
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guest_id")
    private Integer guestId;

    private String name;

    private int age;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Guest() {}

    public Guest(Integer guestId, String name, int age, User user) {
        this.guestId = guestId;
        this.name = name;
        this.age = age;
        this.user = user;
    }

    @Override
    public String toString() {
        return "Guest{" +
                "guestId=" + guestId +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", user=" + (user != null ? user.getUserID() : null) +
                '}';
    }
}
