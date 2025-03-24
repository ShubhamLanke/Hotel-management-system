package org.example.controller;

import org.example.entity.Guest;
import org.example.entity.User;
import org.example.service.UserService;

import java.util.List;
import java.util.Optional;

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void registerUser(User user) {
        userService.registerUser(user);
    }

    public Optional<User> getUserById(int userId) {
        return userService.getUserById(userId);
    }

    public Optional<User> getUserByEmail(String email) {
        return userService.getUserByEmail(email);
    }

    public boolean authenticateUser(String email, String password) {
        Optional<Boolean> result = userService.authenticateUser(email, password);
        return result.orElse(false);
    }

    public boolean isEmailExists(String email) {
        return userService.isEmailExists(email);
    }

    public int createUser(User user) {
        return userService.createUser(user);
    }

    public void updateUserToInactive(User user) {
        userService.updateUserToInactive(user);
    }

    public void updateUserToActive(User user) {
        userService.updateUserToActive(user);
    }

    public List<User> getAllStaff() {
        return userService.getAllStaff();
    }

    public List<User> getAllAdmins() {
        return userService.getAllAdmins();
    }

    public void addAccompaniedGuest(Guest guest) {
        userService.addAccompaniedGuest(guest);
    }
}
