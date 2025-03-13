package org.example.entity;

import org.example.constants.UserRole;

import java.util.List;

public class GuestUser extends User {
    private List<Guest> accompaniedGuests;

    public GuestUser(int userID, String name, String email, String password, boolean isActive, List<Guest> accompaniedGuests) {
        super(userID, name, email, password, UserRole.GUEST, isActive);
        this.accompaniedGuests = accompaniedGuests;
    }

    public List<Guest> getAccompaniedGuests() {
        return accompaniedGuests;
    }

    public void setAccompaniedGuests(List<Guest> accompaniedGuests) {
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
