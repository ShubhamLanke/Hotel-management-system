package org.example.consoleinterface;

import lombok.extern.log4j.Log4j2;
import org.example.constants.UserRole;
import org.example.controller.UserController;
import org.example.entity.User;
import org.example.utility.Response;
import org.example.utility.Validator;
import org.example.view.AdminDashBoard;
import org.example.view.Menu;

import java.util.InputMismatchException;
import java.util.Scanner;

import static org.example.constants.ResponseStatus.ERROR;

@Log4j2
public class HomeMenuUI {
    private final UserController userController;

    private final AdminDashBoard adminDashBoard;

    private final Menu menu;
    private final UserMenuUI userMenuUI;
    private final StaffMenuUI staffMenuUI;
    private final MenuHandler menuHandler;
    private final Scanner scanner;

    public HomeMenuUI(UserController userController, AdminDashBoard adminDashBoard, UserMenuUI userMenuUI, StaffMenuUI staffMenuUI, MenuHandler menuHandler, Menu menu, Scanner scanner) {
        this.userController = userController;
        this.adminDashBoard = adminDashBoard;
        this.userMenuUI = userMenuUI;
        this.staffMenuUI = staffMenuUI;
        this.menuHandler = menuHandler;
        this.menu = menu;
        this.scanner = scanner;
    }

    public void displayMainMenu() {
        while (1>0) {
            menuHandler.displayMenu("Welcome to StayEase Hotel!", new String[]{
                    "View Available Rooms", "Register User", "Login"
            });

            try {
                int choice = menuHandler.getUserChoice();
                switch (choice) {
                    case 1 -> userMenuUI.viewAvailableRooms();
                    case 2 -> registerUser();
                    case 3 -> loginUser();
                    default -> System.out.println("Invalid choice! Please enter a valid option.");
                }
            } catch (InputMismatchException e) {
                log.error("Invalid input! Please enter a number from above options.");
                scanner.nextLine();
            }
        }
    }

    private void registerUser() {
        String name, email, password, roleInput;
        UserRole role;

        while (true) {
            System.out.print("\nEnter your name (or type 0 to cancel): ");
            name = scanner.nextLine().trim();
            if (name.equals("0")) return;

            if (!Validator.isValidName(name)) {
                log.warn("Invalid name format entered: {}", name);
                System.out.println("❌ Invalid name format. Please enter a valid name.");
            } else {
                name = name.toUpperCase();
                break;
            }
        }
        while (true) {
            System.out.print("Enter your email (or type 0 to cancel): ");
            email = scanner.nextLine().trim();
            if (email.equals("0")) return;

            if (!Validator.isValidEmail(email)) {
                log.warn("Invalid email format entered: {}", email);
                System.out.println("❌ Invalid email format. Please enter a valid email.");
            } else {
                email = email.toLowerCase();
                break;
            }
        }
        while (true) {
            System.out.print("Enter your password (or type 0 to cancel): ");
            password = scanner.nextLine().trim();
            if (password.equals("0")) return;

            if (!Validator.isValidPassword(password)) {
                log.warn("Invalid password format entered for email: {}", email);
                System.out.println("❌ Invalid password. Use at least 1 lowercase, 1 uppercase, 1 digit, 1 special char, and min 4 chars.");
            } else {
                break;
            }
        }
        while (true) {
            System.out.print("Enter Role (STAFF/GUEST) (or type 0 to cancel): ");
            roleInput = scanner.nextLine().trim().toUpperCase();
            if (roleInput.equals("0")) return;

            try {
                role = UserRole.valueOf(roleInput);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Invalid role! Please enter either STAFF or GUEST.");
                log.warn("Invalid role entered: {}", roleInput);
            }
        }
        try {
            Response userResponse = userController.isEmailExists(email);
            if (userResponse.getData().equals(true)) {
                System.out.println("❗ This email is already registered. Please use a different email.");
                return;
            }

            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            user.setUserRole(role);

            if (role == UserRole.GUEST) {
                user.setActive(true);
            } else {
                user.setActive(false);
                System.out.println("✅ Staff registration request submitted! Awaiting admin approval.");
            }
            Response registerUserResponse = userController.registerUser(user);
            if (registerUserResponse.getStatus().equals(ERROR)) {
                log.error("Unable to register user.");
                return;
            }
            System.out.println("\n🎉 Congratulations " + user.getName() + " (" + user.getUserRole() + ")" + "! You can now log in.");

        } catch (Exception e) {
            log.error("Unexpected error occurred during registration.", e);
        }
    }

    private void loginUser() {
        while (true) {
            System.out.print("\nEnter email (or 0 to cancel): ");
            String email = scanner.nextLine().toLowerCase();

            if (email.equals("0")) return;

            if (!Validator.isValidEmail(email)) {
                log.warn("Invalid email format entered: {}", email);
                System.out.println("❌ Invalid email format. Please enter a valid email.");
                continue;
            }

            System.out.print("Enter password (or 0 to cancel): ");
            String password = scanner.nextLine();

            if (password.equals("0")) return;

            Response authResponse = userController.authenticateUser(email, password);
            if (!authResponse.isSuccess()) {
                log.warn("Invalid login attempt for email: {}", email);
                System.out.println("❌ Invalid credentials. Please try again.");
                continue;
            }

            Response userResponse = userController.getUserByEmail(email);
            if (!userResponse.isSuccess()) {
                log.warn("User not found after successful authentication: {}", email);
                System.out.println("❗ User not found. Please try again.");
                continue;
            }

            User user = (User) userResponse.getData();

            if (user.getUserRole() != UserRole.SUPER_ADMIN && !user.isActive()) {
                log.warn("Inactive account login attempt: {}", email);
                System.out.println("❗ " + user.getName() + ", your account is currently inactive. Please contact the administrator.");
                return;
            }

            log.info("Login successful: {} ({})", user.getName(), user.getUserRole());
            System.out.println("\n✅ Login successful "+user.getName() + " (" + user.getUserRole() + ")");

            switch (user.getUserRole()) {
                case STAFF -> staffMenuUI.displayStaffMenu(user);
                case GUEST -> userMenuUI.displayUserMenu(user);
                case ADMIN -> adminDashBoard.displayAdminMenu(user);
                case SUPER_ADMIN -> adminDashBoard.displaySuperAdminMenu(user);
                default -> {
                    log.error("Unknown user role for user: {}", email);
                    System.out.println("❗ Unknown user role. Contact support.");
                }
            }
            break;
        }
    }
}
