package org.example.consoleinterface;

import lombok.extern.log4j.Log4j2;
import org.example.constants.UserRole;
import org.example.controller.BookingController;
import org.example.controller.InvoiceController;
import org.example.controller.RoomController;
import org.example.controller.UserController;
import org.example.entity.Booking;
import org.example.entity.Room;
import org.example.entity.User;
import org.example.utility.MenuHandler;
import org.example.utility.PrintGenericResponse;
import org.example.utility.Response;
import org.example.utility.Validator;

import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import static org.example.constants.ResponseStatus.ERROR;
import static org.example.constants.ResponseStatus.SUCCESS;

@Log4j2
public class AdminMenuUI {

    private final MenuHandler menuHandler;
    private final RoomController roomController;
    private final UserController userController;
    private final BookingController bookingController;
    private final InvoiceController invoiceController;

    private final PrintGenericResponse printGenericResponse;
    DateTimeFormatter showDateInFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final Scanner scanner;

    public AdminMenuUI(MenuHandler menuHandler, RoomController roomController, UserController userController, BookingController bookingController, InvoiceController invoiceController, PrintGenericResponse printGenericResponse, Scanner scanner) {
        this.menuHandler = menuHandler;
        this.roomController = roomController;
        this.userController = userController;
        this.bookingController = bookingController;
        this.invoiceController = invoiceController;
        this.printGenericResponse = printGenericResponse;
        this.scanner = scanner;
    }


    public void displaySuperAdminMenu(User loggedInSuperAdmin) {
        if (loggedInSuperAdmin == null || loggedInSuperAdmin.getUserRole() != UserRole.SUPER_ADMIN) {
            System.out.println("Error: No Super Admin is logged in!");
            return;
        }
        System.out.println("===============================");
        System.out.println("Welcome, " + loggedInSuperAdmin.getName() + "!");
        System.out.println("Role: " + loggedInSuperAdmin.getUserRole());
        while (true) {
            menuHandler.displayMenu("Super Admin Dashboard", new String[]{
                    "View Available Rooms", "View All Bookings", "Manage Rooms", "Manage Staff", "Manage Admin", "Logout"});
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
                        System.out.println("You have been logged out successfully.");
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
        while (true) {
            menuHandler.displayMenu("Manage Admins", new String[]{"View All Admins", "Grant or Revoke Admin Access", "Return Back to Menu"});
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
                        System.out.println("❗ Invalid option. Please choose 1, 2, or 3.");
                }
            } catch (InputMismatchException e) {
                System.out.println("❗ Invalid input! Please enter a number.");
                scanner.nextLine();
            }
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
        List<String> ignore = List.of("password");
        printGenericResponse.printTable(users, ignore);
        System.out.println("=======================================================================================");
        System.out.printf("%-10s %-20s %-30s %-15s %-10s%n", "User ID", "Name", "Email", "Role", "Active");
        System.out.println("=======================================================================================");

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

        System.out.println("---------------------------------------------------------------------------------------");
        log.info("Total admins found: {}", users.size());
    }

    private void grantOrRevokeAdminAccess() {
        log.info("Starting admin access grant/revoke process.");

        System.out.print("Enter admin email ID to approve or deny (or 0 to cancel): ");
        String email = scanner.nextLine().trim().toLowerCase();
        if (email.equals("0")) return;

        if (!Validator.isValidEmail(email)) {
            System.out.println("Invalid email format. Please enter a valid email.");
            log.warn("Invalid email format entered: {}", email);
            return;
        }

        Response userResponse = userController.getUserByEmail(email);
        if (!SUCCESS.equals(userResponse.getStatus())) {
            System.out.println("User not found.");
            log.warn("User not found with email: {}", email);
            return;
        }

        User user = (User) userResponse.getData();

        System.out.print("1. Grant Admin Access\n2. Revoke Admin Access\n0. Cancel\nEnter choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                user.setActive(true);
                user.setUserRole(UserRole.ADMIN);
                userController.updateUserToActive(user);
                System.out.println("✅ Admin access granted to " + user.getName());
                log.info("Admin access granted to user: {}", user.getEmail());
                break;

            case 2:
                user.setActive(false);
                userController.updateUserToInactive(user);
                System.out.println("❌ Admin access revoked for " + user.getName());
                log.info("Admin access revoked for user: {}", user.getEmail());
                break;

            case 0:
                System.out.println("Action cancelled.");
                log.info("❗ Admin access grant/revoke cancelled by user.");
                break;

            default:
                System.out.println("Invalid option. Please enter 1, 2, or 0.");
                log.warn("Invalid admin action choice: {}", choice);
        }
    }


    public void displayAdminMenu(User loggedInAdmin) {
        if (loggedInAdmin == null || loggedInAdmin.getUserRole() != UserRole.ADMIN) {
            System.out.println("Error: No admin is logged in!");
            return;
        }
        System.out.println("============================");
        System.out.println("Welcome, " + loggedInAdmin.getName() + "!");
        System.out.println("Role: " + loggedInAdmin.getUserRole());
        while (true) {
            menuHandler.displayMenu("Admin Dashboard", new String[]{
                    "View Available Rooms", "View All Bookings", "Manage Rooms", "Manage Staff", "Logout"});
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
                        System.out.println("You have been logged out successfully.");
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
        List<Room> availableRooms = (List<Room>) roomResponse.getData();
        printGenericResponse.printTable(availableRooms, null);

//        System.out.println("\n================================================================");
//        System.out.printf("%-10s %-15s %-15s %-10s %-15s%n",
//                "Room ID", "Room Number", "Room Type", "Price", "Available");
//        System.out.println("================================================================");
//
//        availableRooms.forEach(room -> System.out.printf("%-10d %-15d %-15s Rs.%-9.2f %-15s%n",
//                room.getRoomID(),
//                room.getRoomNumber(),
//                room.getRoomType().toString(),
//                room.getPrice(),
//                room.isAvailable() ? "Yes" : "No"));
//
//        System.out.println("----------------------------------------------------------------");
        log.info("Displayed {} available rooms.", availableRooms.size());
    }

    private void viewAllBookings() {
        Response bookingResponse = bookingController.getAllBookings();
        if (bookingResponse.getStatus().equals(ERROR)) {
            System.out.println("\nNo bookings found.");
            return;
        }
        List<Booking> bookings = (List<Booking>) bookingResponse.getData();
        printAllBookings(bookings);
    }

    private void printAllBookings(List<Booking> bookings) {
        System.out.println("\n=================================================================================================");
        System.out.printf("%-10s %-10s %-10s %-25s %-25s %-15s%n",
                "BookingID", "UserID", "RoomID", "Check-In", "Check-Out", "Status");
        System.out.println("=================================================================================================");
        for (Booking booking : bookings) {
            System.out.printf("%-10d %-10d %-10d %-25s %-25s %-15s%n",
                    booking.getBookingId(),
                    booking.getUser().getUserID(),
                    booking.getRoom().getRoomID(),
                    booking.getCheckIn().format(showDateInFormat),
                    booking.getCheckOut().format(showDateInFormat),
                    booking.getStatus().toString());
        }
        System.out.println("-------------------------------------------------------------------------------------------------");
    }

    private void manageStaffs() {
        while (true) {
            menuHandler.displayMenu("Manage Staff", new String[]{"View All Staff", "Approve or Deny Staff Registration", "Grant or Revoke Staff Access", "Return back to menu"});
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        getAllStaff();
                        break;
                    case 2:
                        approveOrDenyStaffRegistration();
                        break;
                    case 3:
                        grantOrRevokeStaffAccess();
                        break;
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
    }

    private void getAllStaff() {
        Response userResponse = userController.getAllStaff();
        List<User> users = (List<User>) userResponse.getData();
        if (userResponse.getStatus().equals(ERROR)) {
            System.out.println("\nNo users found.");
            return;
        }
        printGenericUsers(users);
    }

    private void printGenericUsers(List<User> users) {
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

        if (!Validator.isValidEmail(email)) {
            log.warn("Invalid email format entered: {}", email);
            System.out.println("❗ Invalid email format. Please enter a valid email.");
            return;
        }

        Response userResponse = userController.getUserByEmail(email);
        if (userResponse.getStatus().equals(ERROR)) {
            log.error("User not found for email: {}", email);
            System.out.println("❌ User not found.");
            return;
        }

        User user = (User) userResponse.getData();
        System.out.print("Approve staff registration?\n1. Approve\n2. Deny\n0. Cancel \nEnter option: ");
        int option = scanner.nextInt();
        scanner.nextLine();

        switch (option) {
            case 1:
                user.setActive(true);
                userController.updateUserToActive(user);
                log.info("Staff registration approved: {}", user.getEmail());
                System.out.println("✅ Staff " + user.getName() + " registration approved.");
                break;
            case 2:
                log.info("Staff registration denied: {}", user.getEmail());
                System.out.println("❌ Staff " + user.getName() + " registration denied.");
                break;

            case 0:
                System.out.println("Action cancelled.");
                log.info("❗ Admin access grant/revoke cancelled by user.");
                break;

            default:
                log.warn("Invalid option entered: {}", option);
                System.out.println("❗ Invalid option. Please enter 1 (Approve) or 2 (Deny).");
        }
    }

    private void grantOrRevokeStaffAccess() {
        System.out.print("Enter staff email ID to grant or revoke access: ");
        String email = scanner.nextLine().trim().toLowerCase();

        if (!Validator.isValidEmail(email)) {
            log.warn("Invalid email format entered: {}", email);
            System.out.println("❗ Invalid email format. Please enter a valid email.");
            return;
        }

        Response userResponse = userController.getUserByEmail(email);
        if (userResponse.getStatus().equals(ERROR)) {
            log.warn("User with email '{}' not found.", email);
            System.out.println("❌ User not found.");
            return;
        }

        User user = (User) userResponse.getData();
        System.out.print("Staff Access Permission?\n1. Grant Access\n2. Revoke Access\n0. Cancel \nEnter option: ");
        int option = scanner.nextInt();
        scanner.nextLine(); // consume leftover newline

        switch (option) {
            case 1:
                user.setActive(true);
                userController.updateUserToActive(user);
                log.info("Access granted to staff: {}", user.getEmail());
                System.out.println("✅ " + user.getName() + " granted staff access.\n");
                break;
            case 2:
                user.setActive(false);
                userController.updateUserToInactive(user);
                log.info("Access revoked for staff: {}", user.getEmail());
                System.out.println("❌ " + user.getName() + " access revoked.");
                break;

            case 0:
                System.out.println("Action cancelled.");
                log.info("❗ Admin access grant/revoke cancelled by user.");
                break;

            default:
                log.warn("Invalid input '{}' for granting/revoking access.", option);
                System.out.println("❗ Invalid option. Please enter 1 (Grant) or 2 (Revoke).");
        }
    }

    private void manageRooms() {
        while (true) {
            menuHandler.displayMenu("Manage Rooms", new String[]{"Mark Room Under Maintenance", "Mark Room as Active", "Return Back To Menu"});
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
                        System.out.println("❗ Returning to main menu!\n");
                        return;
                    default:
                        System.out.println("❗ Invalid choice. Please enter a number between 1 and 3.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❗ Invalid input. Please enter a valid number.");
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

        System.out.println("\n==================================");
        System.out.println("         Available Rooms         ");
        System.out.println("==================================");
        List<String> ignore = List.of("roomType", "price", "isAvailable");
        printGenericResponse.printTable(availableRooms, ignore);

        System.out.print("Enter Room ID to mark under maintenance (or press 0 to cancel): ");
        String input = scanner.nextLine().trim();
        if ("0".equals(input)) {
            System.out.println("❌ Operation Cancelled\n");
            return;
        }

        try {
            int roomId = Integer.parseInt(input);
            roomController.markRoomUnderMaintenance(roomId);
            System.out.println("✅ Room " + roomId + " marked as under maintenance.");
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

        System.out.print("Enter Room ID to mark as available (or press 0 to cancel): ");
        String input = scanner.nextLine().trim();
        if ("0".equals(input)) {
            System.out.println("❌ Operation Cancelled\n");
            return;
        }
        try {
            int roomId = Integer.parseInt(input);
            roomController.markRoomAvailable(roomId);
            System.out.println("✅ Room " + roomId + " is now available.");
            log.info("Room {} marked as available.", roomId);
        } catch (NumberFormatException e) {
            System.out.println("Invalid Room ID. Please enter a valid number.");
            log.warn("Invalid Room ID input: {}", e.getMessage());
        }
    }
}
