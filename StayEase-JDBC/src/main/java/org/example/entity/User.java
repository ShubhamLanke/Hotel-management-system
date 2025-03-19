package org.example.entity;

import org.example.constants.UserRole;

public class User {

    private Integer userID;
    private String name;
    private String email;
    private String password;
    private UserRole userRole;
    private boolean isActive;

    public User() {
    }

    public User(Integer userID, String name, String email, String password, UserRole userRole, boolean isActive) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.userRole = userRole;
        this.isActive = isActive;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", userRole=" + userRole +
                ", isActive=" + isActive +
                '}';
    }
}
