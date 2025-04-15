package org.example.consoleinterface;

import lombok.extern.log4j.Log4j2;
import org.example.constants.UserRole;
import org.example.controller.BookingController;
import org.example.controller.InvoiceController;
import org.example.controller.RoomController;
import org.example.controller.UserController;
import org.example.entity.Booking;
import org.example.entity.User;
import org.example.utility.PrintGenericResponse;
import org.example.utility.Response;
import org.example.view.Menu;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

@Log4j2
public class StaffMenuUI {
    private final UserController userController;
    private final RoomController roomController;
    private final BookingController bookingController;
    private final InvoiceController invoiceController;

    private final PrintGenericResponse printGenericResponse;
    private final MenuHandler menuHandler;
    private final Menu menu;
    private final Scanner scanner;

    public StaffMenuUI(UserController userController, RoomController roomController, BookingController bookingController, InvoiceController invoiceController, PrintGenericResponse printGenericResponse, MenuHandler menuHandler, Menu menu, Scanner scanner) {
        this.userController = userController;
        this.roomController = roomController;
        this.bookingController = bookingController;
        this.invoiceController = invoiceController;
        this.printGenericResponse = printGenericResponse;
        this.menuHandler = menuHandler;
        this.menu = menu;
        this.scanner = scanner;
    }


    public void displayStaffMenu(User loggedInStaff) {
        log.info("Staff menu accessed by: {} (Role: {})", loggedInStaff.getName(), loggedInStaff.getUserRole());
        if (Boolean.FALSE.equals(loggedInStaff.getUserRole().equals(UserRole.STAFF))) {
            System.out.println("Invalid role type!");
            log.error("Invalid role type!");
            return;
        }
        while (1>0) {
            System.out.println("\n==================================");
            System.out.println("           Staff Menu             ");
            System.out.println("==================================");
            System.out.println("Welcome, " + loggedInStaff.getName() + "!");
            System.out.println("Role: " + loggedInStaff.getUserRole());
            System.out.println("==================================");
            System.out.println("1. Check Guest Details");
            System.out.println("2. View Available Rooms");
            System.out.println("3. Book a Room");
            System.out.println("4. Checkout");
            System.out.println("5. Cancel Booking");
            System.out.println("6. Generate Invoices");
            System.out.println("7. Logout");
            System.out.println("8. Exit");
            System.out.println("----------------------------------");
            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();
                if (scanner.hasNextLine()) {
                    scanner.nextLine();
                }

                switch (choice) {
                    case 1 -> {
                        log.info("User {} selected: Check Guest Details", loggedInStaff.getName());
                        searchUserDetails();
                    }
                    case 2 -> {
                        log.info("User {} selected: View Available Rooms", loggedInStaff.getName());
                        menu.viewAvailableRooms();
                    }
                    case 3 -> {
                        log.info("User {} selected: Book a Room", loggedInStaff.getName());
                        menu.bookRoomByStaff();
                    }
                    case 4 -> {
                        log.info("User {} selected: Checkout", loggedInStaff.getName());
                        menu.checkoutByStaff();
                    }
                    case 5 -> {
                        log.info("User {} selected: Cancel Booking", loggedInStaff.getName());
                        menu.cancelBooking();
                    }
                    case 6 -> {
                        log.info("User {} selected: Generate Invoices", loggedInStaff.getName());
                        menu.generateInvoiceByBookingId();
                    }
                    case 7 -> {
                        log.info("User {} is logging out", loggedInStaff.getName());
                        System.out.println("Logging out...");
                        return;
                    }
                    case 8 -> {
                        log.info("User {} is exiting the system", loggedInStaff.getName());
                        System.out.println("Exiting...");
                        System.exit(0);
                    }
                    default -> {
                        log.warn("Invalid menu choice by user {}: {}", loggedInStaff.getName(), choice);
                        System.out.println("Invalid Choice! Please Choose Between 1 to 8 Only.");
                    }
                }
            } catch (InputMismatchException e) {
                log.error("InputMismatchException: Invalid input by user {} in staff menu", loggedInStaff.getName());
                System.out.println("Invalid input! Please enter a number between 1 and 8.");
                scanner.nextLine();
            }
        }
    }

    private void searchUserDetails() {
        System.out.print("\nEnter user email: ");
        String email = scanner.nextLine().trim();

        if (email.isEmpty()) {
            System.out.println("Email cannot be empty. Please enter a valid email.");
            return;
        }
        Response userResponse = userController.getUserByEmail(email);
        User user = (User) userResponse.getData();
        if (user != null) {
            displayUserDetails(user);
            displayBookingHistory(user);
        } else {
            System.out.println("User not found.");
        }
    }

    private void displayUserDetails(User user) {
        System.out.println("==================================");
        System.out.println("             User Found           ");
        System.out.println("==================================");
        System.out.println("Name: " + user.getName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Role: " + user.getUserRole());
        System.out.println("==================================");
    }

    private void displayBookingHistory(User user) {
        Response bookingResponse = bookingController.getBookingsByUser(user.getUserID());
        List<Booking> bookings = (List<Booking>) bookingResponse.getData();
        if (Objects.isNull(bookings) || bookings.isEmpty()) {
            System.out.println("No bookings found for this user.");
        } else {
            System.out.println("           Booking History        ");
            System.out.println("----------------------------------");
            bookings.forEach(booking -> {
                System.out.println("Booking ID: " + booking.getBookingId());
                System.out.println("Check-in Date: " + booking.getCheckIn());
                System.out.println("Check-out Date: " + booking.getCheckOut());
                System.out.println("Status: " + booking.getStatus());
                System.out.println("----------------------------------");
            });
        }
    }
}
