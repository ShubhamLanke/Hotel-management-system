package org.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.example.constants.UserRole;

@Getter
@Setter
@Entity
public class User {
    @Id
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
