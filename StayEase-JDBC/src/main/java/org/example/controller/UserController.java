package org.example.controller;

import org.example.constants.ResponseStatus;
import org.example.entity.Guest;
import org.example.entity.User;
import org.example.service.UserService;
import org.example.utility.Response;

import java.util.List;

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public Response registerUser(User user) {
        userService.registerUser(user);
        return new Response(null, ResponseStatus.SUCCESS, "User registered successfully.");
    }

    public Response getUserById(int userId) {
        return userService.getUserById(userId)
                .map(user -> new Response(user, ResponseStatus.SUCCESS, "User found."))
                .orElse(new Response(null, ResponseStatus.ERROR, "User not found."));
    }

    public Response getUserByEmail(String email) {
        return userService.getUserByEmail(email)
                .map(user -> new Response(user, ResponseStatus.SUCCESS, "User found by email."))
                .orElse(new Response(null, ResponseStatus.ERROR, "No user found with this email."));
    }

    public Response authenticateUser(String email, String password) {
        boolean result = userService.authenticateUser(email, password);
        if(result) {
            return new Response(true, ResponseStatus.SUCCESS, "User authentication successful.");
        }else{
            return new Response(false, ResponseStatus.ERROR,"User authentication failed.");
        }
    }

    public Response isEmailExists(String email) {
        boolean exists = userService.isEmailExists(email);
        return new Response(exists, ResponseStatus.SUCCESS, exists ? "Email exists." : "Email does not exist.");
    }

    public Response createUser(User user) {
        int userId = userService.createUser(user);
        return new Response(userId, ResponseStatus.SUCCESS, "User created successfully with ID: " + userId);
    }

    public Response updateUserToInactive(User user) {
        userService.updateUserToInactive(user);
        return new Response(null, ResponseStatus.SUCCESS, "User marked as inactive.");
    }

    public Response updateUserToActive(User user) {
        userService.updateUserToActive(user);
        return new Response(null, ResponseStatus.SUCCESS, "User marked as active.");
    }

    public Response getAllStaff() {
        List<User> staffList = userService.getAllStaff();
        return new Response(staffList, ResponseStatus.SUCCESS, "Staff list retrieved successfully.");
    }

    public Response getAllAdmins() {
        List<User> adminList = userService.getAllAdmins();
        return new Response(adminList, ResponseStatus.SUCCESS, "Admin list retrieved successfully.");
    }

    public Response addAccompaniedGuest(Guest guest) {
        userService.addAccompaniedGuest(guest);
        return new Response(null, ResponseStatus.SUCCESS, "Guest added successfully.");
    }
}
