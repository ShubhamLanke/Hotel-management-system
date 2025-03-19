package org.example.entity;

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

    public Integer getGuestId() {
        return guestId;
    }

    public void setGuestId(Integer guestId) {
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
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
