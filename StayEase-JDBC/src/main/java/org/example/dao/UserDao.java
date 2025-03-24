package org.example.dao;

import org.example.entity.Guest;
import org.example.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    void registerUser(User user);

    Optional<User> loginUser(String email, String password);

    List<User> getAllStaff();

    List<User> getAllAdmins();

    void approveStaff(int userId);

    Optional<User> getUserById(int userId);

    Optional<User> getUserByEmailId(String email);

    boolean isEmailExists(String email);

    int createUser(User user);

    void updateUserToInactive(User user);

    void updateUserToActive(User user);

    void addAccompaniedGuest(Guest guest);
}
