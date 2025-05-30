package org.example.dao;

import org.example.constants.UserRole;
import org.example.entity.Guest;
import org.example.entity.GuestUser;
import org.example.entity.User;
import org.example.utility.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class UserDaoImpl implements UserDao {
    private static final Logger logger = Logger.getLogger(UserDaoImpl.class.getName());
    private final Connection connection = DatabaseConnection.getConnection();

    @Override
    public void registerUser(User user) {
        String sql = "INSERT INTO users (name, email, password, user_role, is_active) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setObject(4, user.getUserRole().name(), Types.OTHER);
            preparedStatement.setBoolean(5, user.getUserRole() == UserRole.STAFF ? false : true);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new RuntimeException("Email is already registered!");
            }
            logger.severe("Error while registering user: " + e.getMessage());
        }
    }

    @Override
    public Optional<User> loginUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ? AND is_active = true";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                UserRole role = UserRole.valueOf(rs.getString("user_role"));
                boolean isActive = rs.getBoolean("is_active");

                if (role == UserRole.GUEST) {
                    List<Guest> guests = getAccompaniedGuests(rs.getInt("user_id"));
                    return Optional.of(new GuestUser(
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            isActive,
                            guests
                    ));
                } else {
                    return Optional.of(new User(
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            role,
                            isActive
                    ));
                }
            }
        } catch (SQLException e) {
            logger.severe("Error during login: " + e.getMessage());
        }
        return Optional.empty();  // Return empty Optional if user is not found
    }

    private List<Guest> getAccompaniedGuests(int userId) {
        List<Guest> guests = new ArrayList<>();
        String sql = "SELECT * FROM guests WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                guests.add(new Guest(rs.getInt("guest_id"), rs.getString("name"), rs.getInt("age"), userId));
            }
        } catch (SQLException e) {
            logger.severe("Error while fetching accompanied guests: " + e.getMessage());
        }
        return guests;
    }

    @Override
    public List<User> getAllStaff() {
        List<User> staffList = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE user_role = 'STAFF'";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                staffList.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        UserRole.STAFF,
                        rs.getBoolean("is_active")
                ));
            }
        } catch (SQLException e) {
            logger.severe("Error while fetching staff: " + e.getMessage());
        }
        return staffList;
    }

    @Override
    public List<User> getAllAdmins() {
        List<User> adminList = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE user_role = 'ADMIN'";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                adminList.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        UserRole.ADMIN,
                        rs.getBoolean("is_active")
                ));
            }
        } catch (SQLException e) {
            logger.severe("Error while fetching admins: " + e.getMessage());
        }
        return adminList;
    }

    @Override
    public void approveStaff(int userId) {
        String sql = "UPDATE users SET is_active = true WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Error while approving staff: " + e.getMessage());
        }
    }

    @Override
    public Optional<User> getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new User(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        UserRole.valueOf(rs.getString("user_role")),
                        rs.getBoolean("is_active")
                ));
            }
        } catch (SQLException e) {
            logger.severe("Error while fetching user by ID: " + e.getMessage());
        }
        return Optional.empty();  // Return empty Optional if user is not found
    }

    @Override
    public Optional<User> getUserByEmailId(String emailId) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, emailId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new User(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        UserRole.valueOf(rs.getString("user_role")),
                        rs.getBoolean("is_active")
                ));
            }
        } catch (SQLException e) {
            logger.severe("Error while fetching user by email ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.severe("Error while checking if email exists: " + e.getMessage());
        }
        return false;
    }

    @Override
    public int createUser(User user) {
        String query = "INSERT INTO users (name, email, password, user_role, is_active) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setObject(4, user.getUserRole().name(), Types.OTHER);
            preparedStatement.setBoolean(5, user.isActive());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            logger.severe("Error while creating user: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public void updateUserToInactive(User user) {
        String sql = "UPDATE users SET is_active = false WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, user.getUserID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Error while updating user to inactive: " + e.getMessage());
        }
    }

    @Override
    public void updateUserToActive(User user) {
        String sql = "UPDATE users SET is_active = true, user_role = ? WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, user.getUserRole().toString(), Types.OTHER);
            stmt.setInt(2, user.getUserID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Error while updating user to active: " + e.getMessage());
        }
    }

    @Override
    public void addAccompaniedGuest(Guest guest) {
        String sql = "INSERT INTO guests(user_id, name, age) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, guest.getUserId());
            preparedStatement.setString(2, guest.getName());
            preparedStatement.setInt(3, guest.getAge());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Error while adding accompanied guest: " + e.getMessage());
        }
    }
}
