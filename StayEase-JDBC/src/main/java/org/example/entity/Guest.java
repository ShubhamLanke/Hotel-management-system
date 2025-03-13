package org.example.entity;

public class Guest {
    private int guestId;
    private String name;
    private int age;
    private int userId;

    public Guest(){}

    public Guest(int guestId, String name, int age, int userId) {
        this.guestId = guestId;
        this.name = name;
        this.age = age;
        this.userId = userId;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
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
