package org.example.consoleinterface;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.constants.UserRole;
import org.example.controller.*;
import org.example.entity.User;
import java.util.Scanner;

public abstract class AbstractUI {
//    protected static final Logger log = LogManager.getLogger(AbstractUI.class);
//    protected final Scanner scanner;
//    protected final RoomController roomController;
//    protected final UserController userController;
//    protected final BookingController bookingController;
//    protected final InvoiceController invoiceController;
//
//    public AbstractUI(RoomController roomController, UserController userController, BookingController bookingController, InvoiceController invoiceController, Scanner scanner) {
//        this.roomController = roomController;
//        this.userController = userController;
//        this.bookingController = bookingController;
//        this.invoiceController = invoiceController;
//        this.scanner = scanner;
//    }
//
//    public abstract void displayMenu(User loggedInUser);
//}
//
//class SuperAdminDashboard extends AbstractUI {
//    public SuperAdminDashboard(RoomController roomController, UserController userController, BookingController bookingController, InvoiceController invoiceController, Scanner scanner) {
//        super(roomController, userController, bookingController, invoiceController, scanner);
//    }
//
//    @Override
//    public void displayMenu(User loggedInUser) {
//        if (loggedInUser == null || loggedInUser.getUserRole() != UserRole.SUPER_ADMIN) {
//            System.out.println("Error: No Super Admin is logged in!");
//            return;
//        }
//
//        MenuHandler menuHandler = new MenuHandler(scanner);
//        while (true) {
//            menuHandler.displayMenu("Super Admin Dashboard", new String[]{
//                    "View Available Rooms", "View All Bookings", "Manage Rooms", "Manage Staff", "Manage Admins", "Logout"
//            });
//
//            int choice = menuHandler.getUserChoice();
//            switch (choice) {
//                case 1 -> roomController.viewAvailableRooms();
//                case 2 -> bookingController.viewAllBookings();
//                case 3 -> roomController.manageRooms();
//                case 4 -> userController.manageStaff();
//                case 5 -> userController.manageAdmins();
//                case 6 -> {
//                    System.out.println("Logging out...");
//                    return;
//                }
//                default -> System.out.println("Invalid choice! Please enter a valid option.");
//            }
//        }
//    }
//}
//
//class AdminDashboard extends AbstractUI {
//    public AdminDashboard(RoomController roomController, UserController userController, BookingController bookingController, InvoiceController invoiceController, Scanner scanner) {
//        super(roomController, userController, bookingController, invoiceController, scanner);
//    }
//
//    @Override
//    public void displayMenu(User loggedInUser) {
//        if (loggedInUser == null || loggedInUser.getUserRole() != UserRole.ADMIN) {
//            System.out.println("Error: No Admin is logged in!");
//            return;
//        }
//
//        MenuHandler menuHandler = new MenuHandler(scanner);
//        while (true) {
//            menuHandler.displayMenu("Admin Dashboard", new String[]{
//                    "View Rooms", "View Bookings", "Manage Rooms", "Logout"
//            });
//
//            int choice = menuHandler.getUserChoice();
//            switch (choice) {
//                case 1 -> roomController.viewAvailableRooms();
//                case 2 -> bookingController.viewAllBookings();
//                case 3 -> roomController.manageRooms();
//                case 4 -> {
//                    System.out.println("Logging out...");
//                    return;
//                }
//                default -> System.out.println("Invalid choice! Please enter a valid option.");
//            }
//        }
//    }
//}
//
//// Staff Dashboard
//class StaffDashboard extends AbstractUI {
//    public StaffDashboard(RoomController roomController, UserController userController, BookingController bookingController, InvoiceController invoiceController, Scanner scanner) {
//        super(roomController, userController, bookingController, invoiceController, scanner);
//    }
//
//    @Override
//    public void displayMenu(User loggedInUser) {
//        if (loggedInUser == null || loggedInUser.getUserRole() != UserRole.STAFF) {
//            System.out.println("Error: No Staff is logged in!");
//            return;
//        }
//
//        MenuHandler menuHandler = new MenuHandler(scanner);
//        while (true) {
//            menuHandler.displayMenu("Staff Dashboard", new String[]{
//                    "View Assigned Bookings", "Update Booking Status", "Logout"
//            });
//
//            int choice = menuHandler.getUserChoice();
//            switch (choice) {
//                case 1 -> bookingController.viewAssignedBookings();
//                case 2 -> bookingController.updateBookingStatus();
//                case 3 -> {
//                    System.out.println("Logging out...");
//                    return;
//                }
//                default -> System.out.println("Invalid choice! Please enter a valid option.");
//            }
//        }
//    }
}
