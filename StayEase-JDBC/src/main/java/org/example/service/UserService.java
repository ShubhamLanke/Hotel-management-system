package org.example.service;

import org.example.entity.Guest;
import org.example.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void registerUser(User user);

    Optional<User> getUserById(int userId);

    List<User> getAllStaff();

    List<User> getAllAdmins();

    boolean authenticateUser(String email, String password);

    Optional<User> getUserByEmail(String email);

    boolean isEmailExists(String email);

    int createUser(User user);

    void updateUserToInactive(User user);

    void updateUserToActive(User user);

    void addAccompaniedGuest(Guest guest);
}
