package org.example.view;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.constants.ResponseStatus;
import org.example.constants.UserRole;
import org.example.controller.BookingController;
import org.example.controller.InvoiceController;
import org.example.controller.RoomController;
import org.example.controller.UserController;
import org.example.entity.Booking;
import org.example.entity.Room;
import org.example.entity.User;
import org.example.utility.Response;

import java.awt.print.Book;
import java.util.*;
import java.util.regex.Pattern;

import static org.example.constants.ResponseStatus.ERROR;
import static org.example.constants.ResponseStatus.SUCCESS;

public class AdminDashBoard {
    private final RoomController roomController;
    private final UserController userController;
    private final BookingController bookingController;
    private final InvoiceController invoiceController;

    private static final Logger log = LogManager.getLogger(AdminDashBoard.class);
    private final Scanner scanner;
//    private User loggedInAdmin;


    public AdminDashBoard(RoomController roomController, UserController userController, BookingController bookingController, InvoiceController invoiceController, Scanner scanner) {
        this.roomController = roomController;
        this.userController = userController;
        this.bookingController = bookingController;
        this.invoiceController = invoiceController;
        this.scanner = scanner;
    }

//    public void setLoggedInAdmin(User admin) {
//        this.loggedInAdmin = admin;
//    }

    public void displaySuperAdminMenu(User loggedInSuperAdmin) {
        if (loggedInSuperAdmin == null || loggedInSuperAdmin.getUserRole() != UserRole.SUPER_ADMIN) {
            System.out.println("Error: No Super Admin is logged in!");
            return;
        }

        while (true) { // TODO update code to generic for accepting options as argument
            System.out.println("\n===== Super Admin Dashboard =====");
            System.out.println("Welcome, " + loggedInSuperAdmin.getName() + "!");
            System.out.println("Role: " + loggedInSuperAdmin.getUserRole());
            System.out.println("===============================");
            System.out.println("1. View Available Rooms");
            System.out.println("2. View All Bookings");
            System.out.println("3. Manage Rooms");
            System.out.println("4. Manage staff");
            System.out.println("5. Manage Admins");
            System.out.println("6. Logout");
            System.out.println("-------------------------------");

            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1:
                        viewAvailableRooms();
                        break;
                    case 2:
                        viewAllBookings();
                        break;
                    case 3:
                        manageRooms();
                        break;
                    case 4:
                        manageStaffs();
                        break;
                    case 5:
                        manageAdmins();
                        break;
                    case 6:
                        System.out.println("Logging out...");
                        return;
                    default:
                        System.out.println("Invalid choice! Please enter a valid option.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    private void manageAdmins() {
        System.out.println("\n=======================================");
        System.out.println("              Manage Admins             ");
        System.out.println("=======================================");
        System.out.println("1. View All admins");
        System.out.println("2. Grant or Revoke admin Access");
        System.out.println("3. Return back to menu");
        System.out.println("---------------------------------------");
        System.out.print("Enter your choice: ");
        try {
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    getAllAdmins();
                    break;
                case 2:
                    grantOrRevokeAdminAccess();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input!");
            scanner.nextLine();
        }
    }

    private void getAllAdmins() {
        log.info("Fetching all admin users.");

        Response userResponse = userController.getAllAdmins(); // No generics
        if (userResponse.getStatus().equals(ERROR)) {
            return;
        }

        List<User> users = (List<User>) userResponse.getData();
        if (Objects.isNull(users)) {
            log.warn("No admin users found!");
            System.out.println("No admin users found!");
        }

        System.out.println("====================================================================================");
        System.out.printf("%-10s %-20s %-30s %-15s %-10s%n", "User ID", "Name", "Email", "Role", "Active");
        System.out.println("====================================================================================");

        users.forEach(user -> {
            System.out.printf("%-10d %-20s %-30s %-15s %-10s%n",
                    user.getUserID(),
                    user.getName(),
                    user.getEmail(),
                    user.getUserRole().toString(),
                    user.isActive() ? "Yes" : "No");

            log.debug("Admin found: ID={} Name={} Email={} Role={} Active={}",
                    user.getUserID(), user.getName(), user.getEmail(), user.getUserRole(), user.isActive());
        });

        System.out.println("------------------------------------------------------------------------------------");
        log.info("Total admins found: {}", users.size());
    }

    private void grantOrRevokeAdminAccess() {
        log.info("Starting admin access grant/revoke process.");

        System.out.print("Enter admin email ID to approve or deny: ");
        String email = scanner.nextLine().trim().toLowerCase();

        if (isValidEmail(email)) {
            log.warn("Invalid email format entered: {}", email);
            System.out.println("Invalid email format. Please enter a valid email.");
            return;
        }

        Response userResponse = userController.getUserByEmail(email);

        if (userResponse.getStatus().equals(SUCCESS)) {
            User user = (User) userResponse.getData();
            System.out.print("Grant or Revoke Admin access? (g/r): ");
            String input = scanner.next().trim().toLowerCase();

            if (input.length() != 1) {
                log.warn("Invalid input length: '{}'. Expected 'g' or 'r'.", input);
                System.out.println("Invalid input. Please enter 'g' for grant or 'r' for revoke.");
                return;
            }

            char choice = input.charAt(0);
            scanner.nextLine();

            if (choice == 'g') {
                user.setActive(true);
                user.setUserRole(UserRole.ADMIN);
                userController.updateUserToActive(user);
                log.info("Admin access granted to user: {}", user.getEmail());
                System.out.println("Admin access granted to " + user.getName());
            } else if (choice == 'r') {
                user.setActive(false);
                userController.updateUserToInactive(user);
                log.info("Admin access revoked for user: {}", user.getEmail());
                System.out.println("Admin access revoked for " + user.getName());
            } else {
                log.error("Invalid choice '{}' entered by user.", choice);
                System.out.println("Invalid option. Please enter 'g' or 'r'.");
            }
        } else {
            log.warn("User not found with email: {}", email);
            System.out.println("User not found.");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return !Pattern.matches(emailRegex, email);
    }

    public void displayAdminMenu(User loggedInAdmin) {
        if (loggedInAdmin == null || loggedInAdmin.getUserRole() != UserRole.ADMIN) {
            System.out.println("Error: No admin is logged in!");
            return;
        }

        while (true) {
            System.out.println("\n===== Admin Dashboard =====");
            System.out.println("Welcome, " + loggedInAdmin.getName() + "!");
            System.out.println("Role: " + loggedInAdmin.getUserRole());
            System.out.println("============================");
            System.out.println("1. View Available Rooms");
            System.out.println("2. View All Bookings");
            System.out.println("3. Manage Rooms");
            System.out.println("4. Manage Staff");
            System.out.println("5. Logout");
            System.out.println("----------------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        viewAvailableRooms();
                        break;
                    case 2:
                        viewAllBookings();
                        break;
                    case 3:
                        manageRooms();
                        break;
                    case 4:
                        manageStaffs();
                        break;
                    case 5:
                        System.out.println("Logging out...");
                        return;
                    default:
                        System.out.println("Invalid choice! Please enter a valid option.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    private void viewAvailableRooms() {
        Response roomResponse = roomController.getAvailableRooms();

        if (roomResponse.getStatus().equals(ERROR)) {
            log.info("No available rooms found.");
            System.out.println("\nNo available rooms found.");
            return;
        }

        System.out.println("\n================================================================");
        System.out.printf("%-10s %-15s %-15s %-10s %-15s%n",
                "Room ID", "Room Number", "Room Type", "Price", "Available");
        System.out.println("================================================================");

        List<Room> availableRooms = (List<Room>) roomResponse.getData();
        availableRooms.forEach(room -> System.out.printf("%-10d %-15d %-15s Rs.%-9.2f %-15s%n",
                room.getRoomID(),
                room.getRoomNumber(),
                room.getRoomType().toString(),
                room.getPrice(),
                room.isAvailable() ? "Yes" : "No"));

        System.out.println("----------------------------------------------------------------");
        log.info("Displayed {} available rooms.", availableRooms.size());
    }

    private void viewAllBookings() {
        Response bookingResponse = bookingController.getAllBookings();
        if (bookingResponse.getStatus().equals(ERROR)) {
            System.out.println("\nNo bookings found.");
            return;
        }
        System.out.println("\n=================================================================================================");
        System.out.printf("%-10s %-10s %-10s %-25s %-25s %-15s%n",
                "BookingID", "UserID", "RoomID", "Check-In", "Check-Out", "Status");
        System.out.println("=================================================================================================");

        List<Booking> bookings = (List<Booking>) bookingResponse.getData();

        for (Booking booking : bookings) {
            System.out.printf("%-10d %-10d %-10d %-25s %-25s %-15s%n",
                    booking.getBookingId(),
                    booking.getUserId(),
                    booking.getRoomId(),
                    booking.getCheckIn().toString(),
                    booking.getCheckOut().toString(),
                    booking.getStatus().toString());
        }
        System.out.println("-------------------------------------------------------------------------------------------------");
    }

    private void manageStaffs() {
        System.out.println("\n=======================================");
        System.out.println("              Manage Staff             ");
        System.out.println("=======================================");
        System.out.println("1. View All Staff");
        System.out.println("2. Approve or Deny Staff Registration");
        System.out.println("3. Grant or Revoke Staff Access");
        System.out.println("4. Return back to menu");
        System.out.println("---------------------------------------");
        System.out.print("Enter your choice: ");
        try {
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    getAllStaff();
                    return;
                case 2:
                    approveOrDenyStaffRegistration();
                    return;
                case 3:
                    grantOrRevokeStaffAccess();
                    return;
                case 4:
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input!");
            scanner.nextLine();
        }
    }


    private void getAllStaff() {
        Response userResponse = userController.getAllStaff();
        List<User> users = (List<User>) userResponse.getData();
        if (userResponse.getStatus().equals(ERROR)) {
            System.out.println("\nNo users found.");
            return;
        }
        System.out.println("\n======================================================================================");
        System.out.printf("%-10s %-20s %-30s %-15s %-10s%n", "User ID", "Name", "Email", "Role", "Active");
        System.out.println("======================================================================================");

        for (User user : users) {
            System.out.printf("%-10d %-20s %-30s %-15s %-10s%n",
                    user.getUserID(),
                    user.getName(),
                    user.getEmail(),
                    user.getUserRole().toString(),
                    user.isActive() ? "Yes" : "No");
        }
        System.out.println("------------------------------------------------------------------------------------");
    }


    private void approveOrDenyStaffRegistration() {
        System.out.print("Enter staff email ID to approve or deny: ");
        String email = scanner.nextLine().trim().toLowerCase();

        if (isValidEmail(email)) {
            log.warn("Invalid email format entered: {}", email);
            System.out.println("Invalid email format. Please enter a valid email.");
            return;
        }
        Response userResponse = userController.getUserByEmail(email);
        if (userResponse.getStatus().equals(ERROR)) {
            log.error("User not found for email: {}", email);
            System.out.println("User not found.");
            return;
        }
        User user = (User) userResponse.getData();
        System.out.print("Approve staff registration? (y/n): ");
        String input = scanner.nextLine().trim();

        if (input.equalsIgnoreCase("y")) {
            user.setActive(true);
            userController.updateUserToActive(user);
            log.info("Staff registration approved: {}", user.getEmail());
            System.out.println("Staff " + user.getName() + " registration approved.");
        } else if (input.equalsIgnoreCase("n")) {
            log.info("Staff registration denied: {}", user.getEmail());
            System.out.println("Staff " + user.getName() + " registration denied.");
        } else {
            log.warn("Invalid choice entered: {}", input);
            System.out.println("Invalid option. Please enter 'y' for yes or 'n' for no.");
        }
    }

    private void grantOrRevokeStaffAccess() {
        System.out.print("Enter staff email ID to approve or deny: ");
        String email = scanner.nextLine().trim().toLowerCase();

        Response userResponse = userController.getUserByEmail(email);
        if (userResponse.getStatus().equals(ERROR)) {
            System.out.println("User not found.");
            log.warn("User with email '{}' not found.", email);
            return;
        }
        User user = (User) userResponse.getData();
        System.out.println("Grant or Revoke access? (g/r): ");
        char choice = scanner.next().charAt(0);
        scanner.nextLine();

        if (choice == 'g' || choice == 'G') {
            user.setActive(true);
            userController.updateUserToActive(user);
            System.out.println(user.getName() + " granted access.");
            log.info("Access granted to staff: {}", user.getEmail());
        } else if (choice == 'r' || choice == 'R') {
            user.setActive(false);
            userController.updateUserToInactive(user);
            System.out.println(user.getName() + " access revoked.");
            log.info("Access revoked for staff: {}", user.getEmail());
        } else {
            System.out.println("Invalid option.");
            log.warn("Invalid input '{}' for granting/revoking access.", choice);
        }
    }

    private void manageRooms() {
        while (true) {
            System.out.println("\n=======================================");
            System.out.println("              Manage Rooms             ");
            System.out.println("=======================================");
            System.out.println("1. Mark Room Under Maintenance");
            System.out.println("2. Mark Room as Active");
            System.out.println("3. Return back to menu");
            System.out.println("---------------------------------------");
            System.out.print("Your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        markRoomUnderMaintenance();
                        break;
                    case 2:
                        markRoomAsActive();
                        break;
                    case 3:
                        System.out.println("Returning to main menu...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 3.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                log.warn("Invalid menu input: {}", e.getMessage());
            }
        }
    }

    private void markRoomUnderMaintenance() {
        Response roomResponse = roomController.getAvailableRooms();
        if (roomResponse.getStatus().equals(ERROR)) {
            System.out.println("\nNo available rooms found.");
            return;
        }
        List<Room> availableRooms = (List<Room>) roomResponse.getData();
        System.out.println("\n=================================");
        System.out.println("         Available Rooms         ");
        System.out.println("=================================");
        availableRooms.forEach(room ->
                System.out.printf("Room ID: %d, Room Number: %d%n", room.getRoomID(), room.getRoomNumber())
        );
        System.out.println("-------------------------------");

        System.out.print("Enter Room ID to mark under maintenance (or press Enter to cancel): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return;

        try {
            int roomId = Integer.parseInt(input);
            roomController.markRoomUnderMaintenance(roomId);
            System.out.println("Room " + roomId + " marked as under maintenance.");
            log.info("Room {} marked under maintenance.", roomId);
        } catch (NumberFormatException e) {
            System.out.println("Invalid Room ID. Please enter a valid number.");
            log.warn("Invalid Room ID input: {}", e.getMessage());
        }
    }

    private void markRoomAsActive() {
        Response roomResponse = roomController.getRoomsUnderMaintenance();
        if (roomResponse.getStatus().equals(ERROR)) {
            System.out.println("\nNo rooms under maintenance found.");
            return;
        }
        List<Room> maintenanceRooms = (List<Room>) roomResponse.getData();

        System.out.println("\n=================================");
        System.out.println("     Rooms Under Maintenance     ");
        System.out.println("=================================");
        maintenanceRooms.forEach(room ->
                System.out.printf("Room ID: %d, Room Number: %d%n", room.getRoomID(), room.getRoomNumber())
        );
        System.out.println("-------------------------------");

        System.out.print("Enter Room ID to mark as available (or press Enter to cancel): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return;

        try {
            int roomId = Integer.parseInt(input);
            roomController.markRoomAvailable(roomId);
            System.out.println("Room " + roomId + " is now available.");
            log.info("Room {} marked as available.", roomId);
        } catch (NumberFormatException e) {
            System.out.println("Invalid Room ID. Please enter a valid number.");
            log.warn("Invalid Room ID input: {}", e.getMessage());
        }
    }
}

