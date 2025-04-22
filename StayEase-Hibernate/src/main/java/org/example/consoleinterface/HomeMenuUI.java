package org.example.consoleinterface;

import lombok.extern.log4j.Log4j2;
import org.example.constants.UserRole;
import org.example.controller.UserController;
import org.example.entity.User;
import org.example.utility.MenuHandler;
import org.example.utility.Response;
import org.example.utility.Validator;

import java.util.InputMismatchException;
import java.util.Scanner;

@Log4j2
public class HomeMenuUI {
    private final UserController userController;

    private final AdminMenuUI adminMenuUI;

    private final UserMenuUI userMenuUI;
    private final StaffMenuUI staffMenuUI;
    private final MenuHandler menuHandler;
    private final Scanner scanner;

    public HomeMenuUI(UserController userController, AdminMenuUI adminMenuUI, UserMenuUI userMenuUI, StaffMenuUI staffMenuUI, MenuHandler menuHandler, Scanner scanner) {
        this.userController = userController;
        this.adminMenuUI = adminMenuUI;
        this.userMenuUI = userMenuUI;
        this.staffMenuUI = staffMenuUI;
        this.menuHandler = menuHandler;
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
                    case 2 -> userMenuUI.registerUser();
                    case 3 -> loginUser();
                    default -> System.out.println("Invalid choice! Please enter a valid option.");
                }
            } catch (InputMismatchException e) {
                log.error("Invalid input! Please enter a number from above options.");
                scanner.nextLine();
            }
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
                case ADMIN -> adminMenuUI.displayAdminMenu(user);
                case SUPER_ADMIN -> adminMenuUI.displaySuperAdminMenu(user);
                default -> {
                    log.error("Unknown user role for user: {}", email);
                    System.out.println("❗ Unknown user role. Contact support.");
                }
            }
            break;
        }
    }
}
//✅❌❗