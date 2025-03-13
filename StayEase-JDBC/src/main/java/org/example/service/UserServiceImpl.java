package org.example.service;

import org.example.dao.UserDao;
import org.example.entity.Guest;
import org.example.entity.User;

import java.util.List;

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
        userDao.registerUser(user);
    }

    @Override
    public User getUserById(int userId) {
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
    public boolean authenticateUser(String email, String password) {
        User user = userDao.getUserByEmailId(email);
        return user != null && user.getPassword().equals(password);
    }

    @Override
    public List<User> getAdmins() {
        return List.of();
    }

    @Override
    public User getUserByEmail(String email) {
        return userDao.getUserByEmailId(email);
    }

    @Override
    public boolean isEmailExists(String email) {
        return userDao.isEmailExists(email);
    }

    @Override
    public int createUser(User user) {
        return userDao.createUser(user);
    }

    @Override
    public void updateUserToInactive(User user) {
        userDao.updateUserToInactive(user);
    }

    @Override
    public void updateUserToActive(User user) {
        userDao.updateUserToActive(user);
    }

    @Override
    public void addAccompaniedGuest(Guest guest) {
        userDao.addAccompaniedGuest(guest);
    }
}

