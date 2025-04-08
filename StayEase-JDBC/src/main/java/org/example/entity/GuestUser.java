package org.example.entity;

import lombok.Getter;
import lombok.Setter;
import org.example.constants.UserRole;

import java.util.List;

@Setter
@Getter
public class GuestUser extends User {
    private List<Guest> accompaniedGuests;

    public GuestUser(Integer userID, String name, String email, String password, boolean isActive, List<Guest> accompaniedGuests) {
        super(userID, name, email, password, UserRole.GUEST, isActive);
        this.accompaniedGuests = accompaniedGuests;
    }

    @Override
    public String toString() {
        return "GuestUser{" +
                "userID=" + getUserID() +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", userRole=" + getUserRole() +
                ", isActive=" + isActive() +
                ", accompaniedGuests=" + (accompaniedGuests != null ? accompaniedGuests.toString() : "[]") +
                '}';
    }
}
