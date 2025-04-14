package org.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.constants.UserRole;

import java.util.List;
import java.util.Objects;

@Setter
@Getter
@Entity
//@DiscriminatorValue("GUEST") // Used with SINGLE_TABLE inheritance, optional if you're using discriminator
public class GuestUser extends User {

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Guest> accompaniedGuests;

    public GuestUser() {
        super.setUserRole(UserRole.GUEST);
    }

    public GuestUser(Integer userID, String name, String email, String password, boolean isActive, List<Guest> accompaniedGuests) {
        super(userID, name, email, password, UserRole.GUEST, isActive);
        this.accompaniedGuests = accompaniedGuests;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GuestUser guestUser = (GuestUser) o;
        return Objects.equals(accompaniedGuests, guestUser.accompaniedGuests);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), accompaniedGuests);
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
