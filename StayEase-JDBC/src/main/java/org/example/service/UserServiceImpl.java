package org.example.service;

import org.example.dao.UserDao;
import org.example.entity.Guest;
import org.example.entity.User;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void registerUser(User user) {
        if (userDao.isEmailExists(user.getEmail())) {
            throw new RuntimeException("Email already registered!");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        userDao.registerUser(user);
    }

    @Override
    public Optional<User> getUserById(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid userId.");
        }
        return userDao.getUserById(userId);
    }

    @Override
    public List<User> getAllStaff() {
        return userDao.getAllStaff();
    }

    @Override
    public List<User> getAllAdmins() {
        return userDao.getAllAdmins();
    }

    @Override
    public Optional<Boolean> authenticateUser(String email, String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Email and password cannot be empty.");
        }

        Optional<Optional<User>> userOpt = Optional.ofNullable(userDao.getUserByEmailId(email));

        return userOpt.map(user -> user.get().getPassword().equals(password));
    }




    @Override
    public Optional<User> getUserByEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }
        return userDao.getUserByEmailId(email);
    }

    @Override
    public boolean isEmailExists(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }

        return userDao.isEmailExists(email);
    }

    @Override
    public int createUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        return userDao.createUser(user);
    }

    @Override
    public void updateUserToInactive(User user) {
        if (user == null || user.getUserID() <= 0) {
            throw new IllegalArgumentException("Invalid user.");
        }
        userDao.updateUserToInactive(user);
    }

    @Override
    public void updateUserToActive(User user) {
        if (user == null || user.getUserID() <= 0) {
            throw new IllegalArgumentException("Invalid user.");
        }
        userDao.updateUserToActive(user);
    }

    @Override
    public void addAccompaniedGuest(Guest guest) {
        if (guest == null || guest.getUserId() <= 0) {
            throw new IllegalArgumentException("Invalid guest details.");
        }
        userDao.addAccompaniedGuest(guest);
    }
}
