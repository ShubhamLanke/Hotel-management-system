package org.example.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.example.constants.UserRole;

@Data
@Entity
@Table(name = "users")
@Builder
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userID;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole userRole;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    public User() {}

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
