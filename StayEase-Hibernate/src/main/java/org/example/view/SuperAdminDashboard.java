package org.example.view;

import org.example.constants.UserRole;
import org.example.entity.Booking;
import org.example.entity.Invoice;
import org.example.entity.Room;
import org.example.entity.User;
import org.example.service.BookingService;
import org.example.service.InvoiceService;
import org.example.service.RoomService;
import org.example.service.UserService;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class SuperAdminDashboard {
//    private final UserService userService;
//    private final RoomService roomService;
//    private final BookingService bookingService;
//    private final InvoiceService invoiceService;
//    private final Scanner scanner;
//    private User loggedInSuperAdmin;
//
//    public SuperAdminDashboard(UserService userService, RoomService roomService,
//                               BookingService bookingService, InvoiceService invoiceService,
//                               Scanner scanner) {
//        this.userService = userService;
//        this.roomService = roomService;
//        this.bookingService = bookingService;
//        this.invoiceService = invoiceService;
//        this.scanner = scanner;
//    }
//
//    public void setLoggedInSuperAdmin(User superAdmin) {
//        this.loggedInSuperAdmin = superAdmin;
//    }
//
//    public void displaySuperAdminMenu() {
//        if (loggedInSuperAdmin == null || loggedInSuperAdmin.getUserRole() != UserRole.SUPER_ADMIN) {
//            System.out.println("Error: No Super Admin is logged in!");
//            return;
//        }
//
//        while (true) {
//            System.out.println("\n===== Super Admin Dashboard =====");
//            System.out.println("Welcome, " + loggedInSuperAdmin.getName() + "!");
//            System.out.println("Role: " + loggedInSuperAdmin.getUserRole());
//            System.out.println("----------------------------");
//            System.out.println("1. View " +
//                    "Available Rooms");
//            System.out.println("2. View All Bookings");
//            System.out.println("3. View All Invoices");
//            System.out.println("4. Manage Users");
//            System.out.println("5. Manage Admins");
//            System.out.println("6. View System Logs");
//            System.out.println("7. Logout");
//
//            System.out.print("Enter your choice: ");
//            try {
//                int choice = scanner.nextInt();
//                scanner.nextLine();
//
//                switch (choice) {
//                    case 1:
//                        viewAvailableRooms();
//                        break;
//                    case 2:
//                        viewAllBookings();
//                        break;
//                    case 3:
//                        viewAllInvoices();
//                        break;
//                    case 4:
//                        manageUsers();
//                        break;
//                    case 5:
//                        manageAdmins();
//                        break;
//                    case 6:
//                        viewSystemLogs();
//                        break;
//                    case 7:
//                        System.out.println("Logging out...");
//                        return;
//                    default:
//                        System.out.println("Invalid choice! Please enter a valid option.");
//                }
//            } catch (InputMismatchException e) {
//                System.out.println("Invalid input! Please enter a number.");
//                scanner.nextLine();
//            }
//        }
//    }
//
//    private void viewAvailableRooms() {
//        List<Room> availableRooms = roomService.getAvailableRooms();
//        if (availableRooms.isEmpty()) {
//            System.out.println("No available rooms found.");
//        } else {
//            availableRooms.forEach(System.out::println);
//        }
//    }
//
//    private void viewAllBookings() {
//        List<Booking> bookings = bookingService.getBookingsByUser(-1); // Fetch all bookings
//        if (bookings.isEmpty()) {
//            System.out.println("No bookings found.");
//        } else {
//            bookings.forEach(System.out::println);
//        }
//    }
//
//    private void viewAllInvoices() {
//        System.out.println("List of all invoices:");
//         List<Invoice> allInvoices = invoiceService.getAllInvoices();
//    }
//
//    private void manageUsers() {
//        System.out.println("1. View All Users");
//        System.out.println("2. Approve or Deny Staff Registration");
//        System.out.println("3. Grant or Revoke Staff Access");
//        System.out.print("Enter your choice: ");
//        try {
//            int choice = scanner.nextInt();
//            scanner.nextLine();
//
//            switch (choice) {
//                case 1:
//                    getAllStaff();
//                    break;
//                case 2:
//                    approveOrDenyStaffRegistration();
//                    break;
//                case 3:
//                    grantOrRevokeStaffAccess();
//                    break;
//                default:
//                    System.out.println("Invalid option.");
//            }
//        } catch (InputMismatchException e) {
//            System.out.println("Invalid input!");
//            scanner.nextLine();
//        }
//    }
//
//    private void manageAdmins() {
//        System.out.println("1. View All Admins");
//        System.out.println("2. Grant Admin Role");
//        System.out.println("3. Revoke Admin Role");
//        System.out.print("Enter your choice: ");
//        try {
//            int choice = scanner.nextInt();
//            scanner.nextLine();
//
//            switch (choice) {
//                case 1:
//                    viewAllAdmins();
//                    break;
//                case 2:
//                    grantAdminRole();
//                    break;
//                case 3:
//                    revokeAdminRole();
//                    break;
//                default:
//                    System.out.println("Invalid option.");
//            }
//        } catch (InputMismatchException e) {
//            System.out.println("Invalid input!");
//            scanner.nextLine();
//        }
//    }
//
//    private void getAllStaff() {
//        List<User> users = userService.getAllStaff(); // Assuming the method to fetch all users
//        if (users.isEmpty()) {
//            System.out.println("No users found.");
//        } else {
//            users.forEach(System.out::println);
//        }
//    }
//
//    private void viewAllAdmins() {
//        List<User> admins = userService.getAdmins(); // Assuming the method to fetch all admins
//        if (admins.isEmpty()) {
//            System.out.println("No admins found.");
//        } else {
//            admins.forEach(System.out::println);
//        }
//    }
//
//    private void approveOrDenyStaffRegistration() {
//        System.out.print("Enter staff user ID to approve or deny: ");
//        int userId = scanner.nextInt();
//        scanner.nextLine();
//
//        User user = userService.getUserById(userId);
//        if (user != null) {
//            System.out.println("Approve staff registration? (y/n): ");
//            char choice = scanner.next().charAt(0);
//            if (choice == 'y' || choice == 'Y') {
//                user.setActive(true);
//                userService.updateUserToInactive(user);
//                System.out.println("Staff registration approved.");
//            } else {
//                user.setUserRole(UserRole.GUEST); // or any default state
//                userService.registerUser(user);
//                System.out.println("Staff registration denied.");
//            }
//        } else {
//            System.out.println("User not found.");
//        }
//    }
//
//    private void grantOrRevokeStaffAccess() {
//        System.out.print("Enter staff user ID to grant or revoke access: ");
//        int userId = scanner.nextInt();
//        scanner.nextLine();
//
//        User user = userService.getUserById(userId);
//        if (user != null) {
//            System.out.println("Grant or Revoke access? (g/r): ");
//            char choice = scanner.next().charAt(0);
//            if (choice == 'g' || choice == 'G') {
//                user.setActive(true);
//                userService.registerUser(user);
//                System.out.println("Staff access granted.");
//            } else if (choice == 'r' || choice == 'R') {
//                user.setActive(false);
//                userService.registerUser(user);
//                System.out.println("Staff access revoked.");
//            } else {
//                System.out.println("Invalid option.");
//            }
//        } else {
//            System.out.println("User not found.");
//        }
//    }
//
//    private void grantAdminRole() {
//        System.out.print("Enter user ID to grant Admin role: ");
//        int userId = scanner.nextInt();
//        scanner.nextLine();
//
//        User user = userService.getUserById(userId);
//        if (user != null) {
//            user.setUserRole(UserRole.ADMIN);
//            userService.registerUser(user);
//            System.out.println("Admin role granted.");
//        } else {
//            System.out.println("User not found.");
//        }
//    }
//
//    private void revokeAdminRole() {
//        System.out.print("Enter user ID to revoke Admin role: ");
//        int userId = scanner.nextInt();
//        scanner.nextLine();
//
//        User user = userService.getUserById(userId);
//        if (user != null && user.getUserRole() == UserRole.ADMIN) {
//            user.setUserRole(UserRole.STAFF); // or another appropriate role
//            userService.registerUser(user);
//            System.out.println("Admin role revoked.");
//        } else {
//            System.out.println("User not found or they are not an admin.");
//        }
//    }
//
//    private void viewSystemLogs() {
//        // Implement a method to view logs
//        // Example: List<Log> logs = logService.getLogs();
//        // logs.forEach(System.out::println);
//    }
}
