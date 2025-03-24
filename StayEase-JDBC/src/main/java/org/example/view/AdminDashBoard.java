package org.example.view;


import org.example.constants.UserRole;
import org.example.controller.BookingController;
import org.example.controller.InvoiceController;
import org.example.controller.RoomController;
import org.example.controller.UserController;
import org.example.entity.Booking;
import org.example.entity.Room;
import org.example.entity.User;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class AdminDashBoard {
    private final RoomController roomController;
    private final UserController userController;
    private final BookingController bookingController;
    private final InvoiceController invoiceController;

    private final Scanner scanner;
    private User loggedInAdmin;

    public AdminDashBoard(RoomController roomController, UserController userController, BookingController bookingController, InvoiceController invoiceController, Scanner scanner) {
        this.roomController = roomController;
        this.userController = userController;
        this.bookingController = bookingController;
        this.invoiceController = invoiceController;
        this.scanner = scanner;
    }

    public void setLoggedInAdmin(User admin) {
        this.loggedInAdmin = admin;
    }

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
//        System.out.println("3. Create New Admin");
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
//                    createNewAdmin();
//                    break;
                default:
                    System.out.println("Invalid option.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input!");
            scanner.nextLine();
        }
    }

    private void getAllAdmins() {
        List<User> users = userController.getAllAdmins();
        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            System.out.println("====================================================================================");
            System.out.printf("%-10s %-20s %-30s %-15s %-10s%n", "User ID", "Name", "Email", "Role", "Active");
            System.out.println("====================================================================================");

//            users.forEach(System.out::printfn);
            for (User user : users) { // TODO replace with forEach
                System.out.printf("%-10d %-20s %-30s %-15s %-10s%n",
                        user.getUserID(),
                        user.getName(),
                        user.getEmail(),
                        user.getUserRole().toString(),
                        user.isActive() ? "Yes" : "No");
            }
            System.out.println("------------------------------------------------------------------------------------");
        }
    }

    private void grantOrRevokeAdminAccess() {
        System.out.print("Enter admin email ID to approve or deny: ");
        String email = scanner.nextLine().toLowerCase();
        Optional<User> user = userController.getUserByEmail(email); // TODO validate Email & regex pattern

        if (user != null) {
            System.out.println("Grant or Revoke Admin access? (g/r): ");
            char choice = scanner.next().charAt(0); // TODO validate char
            if (choice == 'g' || choice == 'G') {
                user.setActive(true);
                user.setUserRole(UserRole.ADMIN);
                userController.updateUserToActive(user);
                System.out.println("Admin access granted to " + user.getName());
            } else if (choice == 'r' || choice == 'R') {
                user.setActive(false);
                userController.updateUserToInactive(user);
                System.out.println("Admin access is revoked for " + user.getName());
            } else {
                System.out.println("Invalid option.");
            }
        } else {
            System.out.println("User not found.");
        }
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
        List<Room> availableRooms = roomController.getAvailableRooms();
        if (availableRooms.isEmpty()) {
            System.out.println("\nNo available rooms found.");
        } else {
            System.out.println("\n================================================================");
            System.out.printf("%-10s %-15s %-15s %-10s %-15s%n",
                    "Room ID", "Room Number", "Room Type", "Price", "Available");
            System.out.println("================================================================");

            for (Room room : availableRooms) { // TODO forEach
                System.out.printf("%-10d %-15d %-15s Rs.%-9.2f %-15s%n",
                        room.getRoomID(),
                        room.getRoomNumber(),
                        room.getRoomType().toString(),
                        room.getPrice(),
                        room.isAvailable() ? "Yes" : "No");
            }
            System.out.println("----------------------------------------------------------------");
        }
    }

    private void viewAllBookings() {
        List<Booking> bookings = bookingController.getAllBookings();
        if (bookings.isEmpty()) {
            System.out.println("\nNo bookings found.");
        } else {
            System.out.println("\n=================================================================================================");
            System.out.printf("%-10s %-10s %-10s %-25s %-25s %-15s%n",
                    "BookingID", "UserID", "RoomID", "Check-In", "Check-Out", "Status");
            System.out.println("=================================================================================================");

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
        List<User> users = userController.getAllStaff();
        if (users.isEmpty()) {
            System.out.println("\nNo users found.");
        } else {
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
    }

    private void approveOrDenyStaffRegistration() {
        System.out.print("Enter staff email ID to approve or deny: ");
        String email = scanner.nextLine();

        User user = userController.getUserByEmail(email);
        if (user != null) {
            System.out.println("Approve staff registration? (y/n): ");
            char choice = scanner.next().charAt(0);
            scanner.nextLine();

            if (choice == 'y' || choice == 'Y') {
                user.setActive(true);
                userController.updateUserToActive(user);
                System.out.println("Staff " + user.getName() + " registration approved.");
            } else {
                System.out.println("Staff " + user.getName() + " registration denied.");
            }
        } else {
            System.out.println("User not found.");
        }
    }


    private void grantOrRevokeStaffAccess() {
        System.out.print("Enter staff email ID to approve or deny: ");
        String email = scanner.nextLine();

        User user = userController.getUserByEmail(email);
        if (user != null) {
            System.out.println("Grant or Revoke access? (g/r): ");
            char choice = scanner.next().charAt(0);
            if (choice == 'g' || choice == 'G') {
                user.setActive(true);
                userController.updateUserToActive(user);
                System.out.println(user.getName() + " granted access.");
            } else if (choice == 'r' || choice == 'R') {
                user.setActive(false);
                userController.updateUserToInactive(user);
                System.out.println(user.getName() + " access revoked.");
            } else {
                System.out.println("Invalid option.");
            }
        } else {
            System.out.println("User not found.");
        }
    }

    private void manageRooms() {
        System.out.println("\n=======================================");
        System.out.println("              Manage Rooms             ");
        System.out.println("=======================================");
        System.out.println("1. Mark Room Under Maintenance");
        System.out.println("2. Mark Room as Active");
        System.out.println("3. Return back to menu");
        System.out.println("---------------------------------------");
        System.out.print("Your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            List<Room> availableRooms = roomController.getAvailableRooms();
            if (availableRooms.isEmpty()) {
                System.out.println("\nNo available rooms found.");
                return;
            }
            System.out.println("\n=================================");
            System.out.println("         Available Rooms         ");
            System.out.println("=================================");
            for (Room room : availableRooms) {
                System.out.printf("Room ID: %d, Room Number: %d%n", room.getRoomID(), room.getRoomNumber());
            }
            System.out.println("-------------------------------");
            System.out.print("Enter Room ID to mark under maintenance (or press Enter to cancel): ");
            String input = scanner.nextLine();
            if (input.isEmpty()) {
                return;
            }
            int roomId = Integer.parseInt(input);
            roomController.markRoomUnderMaintenance(roomId);
            System.out.println("Room " + roomId + " marked as under maintenance (unavailable).");

        } else if (choice == 2) {
            List<Room> maintenanceRooms = roomController.getRoomsUnderMaintenance();
            if (maintenanceRooms.isEmpty()) {
                System.out.println("No rooms under maintenance found.");
                return;
            }
            System.out.println("\n=================================");
            System.out.println("     Rooms Under Maintenance     ");
            System.out.println("=================================");
            for (Room room : maintenanceRooms) {
                System.out.printf("Room ID: %d, Room Number: %d%n", room.getRoomID(), room.getRoomNumber());
            }
            System.out.println("-------------------------------");
            System.out.print("Enter Room ID to mark as available (or press Enter to cancel): ");
            String input = scanner.nextLine();
            if (input.isEmpty()) {
                return;
            }
            int roomId = Integer.parseInt(input);
            roomController.markRoomAvailable(roomId);
            System.out.println("Room " + roomId + " is now marked as available.");

        } else if (choice == 3) {
            return;
        } else {
            System.out.println("Invalid choice. Please enter a number between 1 and 3.");
        }
    }
}
