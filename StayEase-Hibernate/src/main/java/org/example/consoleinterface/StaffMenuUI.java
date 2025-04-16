package org.example.consoleinterface;

import lombok.extern.log4j.Log4j2;
import org.example.constants.BookingStatus;
import org.example.constants.PaymentStatus;
import org.example.constants.ResponseStatus;
import org.example.constants.UserRole;
import org.example.controller.BookingController;
import org.example.controller.InvoiceController;
import org.example.controller.RoomController;
import org.example.controller.UserController;
import org.example.entity.*;
import org.example.utility.PrintGenericResponse;
import org.example.utility.Response;
import org.example.utility.Validator;
import org.example.view.Menu;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Log4j2
public class StaffMenuUI {

    private final UserMenuUI userMenuUI;
    private final UserController userController;
    private final RoomController roomController;
    private final BookingController bookingController;
    private final InvoiceController invoiceController;

    private final PrintGenericResponse printGenericResponse;
    private final MenuHandler menuHandler;
    private final Scanner scanner;

    public StaffMenuUI(UserMenuUI userMenuUI, UserController userController, RoomController roomController, BookingController bookingController, InvoiceController invoiceController, PrintGenericResponse printGenericResponse, MenuHandler menuHandler,Scanner scanner) {
        this.userMenuUI = userMenuUI;
        this.userController = userController;
        this.roomController = roomController;
        this.bookingController = bookingController;
        this.invoiceController = invoiceController;
        this.printGenericResponse = printGenericResponse;
        this.menuHandler = menuHandler;
        this.scanner = scanner;
    }


    public void displayStaffMenu(User loggedInStaff) {
        log.info("Staff menu accessed by: {} (Role: {})", loggedInStaff.getName(), loggedInStaff.getUserRole());
        if (Boolean.FALSE.equals(loggedInStaff.getUserRole().equals(UserRole.STAFF))) {
            System.out.println("Invalid role type!");
            log.error("Invalid role type!");
            return;
        }
        System.out.println("\n==================================");
        System.out.println("Welcome, " + loggedInStaff.getName() + "!");
        System.out.println("Role: " + loggedInStaff.getUserRole());
        while (1 > 0) {
            menuHandler.displayMenu("Staff Menu", new String[]{"Check Guest Details", "View Available Rooms", "Book a Room",
                    "Checkout", "Cancel Booking", "Generate Invoices", "Logout", "Exit" });
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
                        userMenuUI.viewAvailableRooms();
                    }
                    case 3 -> {
                        log.info("User {} selected: Book a Room", loggedInStaff.getName());
                        bookRoomByStaff();
                    }
                    case 4 -> {
                        log.info("User {} selected: Checkout", loggedInStaff.getName());
                        checkoutByStaff();
                    }
                    case 5 -> {
                        log.info("User {} selected: Cancel Booking", loggedInStaff.getName());
                        cancelBooking();
                    }
                    case 6 -> {
                        log.info("User {} selected: Generate Invoices", loggedInStaff.getName());
                        generateInvoiceByBookingId();
                    }
                    case 7 -> {
                        log.info("User {} is logging out", loggedInStaff.getName());
                        System.out.println("You have been logged out successfully.");
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

    private void generateInvoiceByBookingId() {
        log.info("Generating invoice by booking ID.");
        System.out.print("Enter Booking ID to generate invoice: ");
        int bookingId = scanner.nextInt();

        Response invoiceResponse = invoiceController.getInvoiceByBookingId(bookingId);
        Invoice invoice = (Invoice) invoiceResponse.getData();
        if (Objects.isNull(invoice)){
            System.out.println("No invoice found for this user.");
        }
        if (!validateInvoice(invoice)) return;

        Response bookingResponse = bookingController.getBookingById(bookingId);
        Booking booking = (Booking) bookingResponse.getData();
        if (!bookingResponse.isSuccess()) return;

        printGenericResponse.printInvoice(booking, invoice);
        displayInvoice(booking, invoice);
    }

    private boolean validateInvoice(Invoice invoice) {
        if (invoice == null) {
            log.warn("Invoice not found.");
            System.out.println("Invoice not found for the given Booking ID.");
            return false;
        }
        return true;
    }

    private void displayInvoice(Booking booking, Invoice invoice) {
        System.out.println("\n------ INVOICE ------");
        System.out.printf("Booking ID: %d%nUser Email: %s%nRoom Number: %d%nTotal Amount: %.2f%nBooking Status: %s%nPayment Status: %s%nCheck-in Date: %s%nCheck-out Date: %s%n",
                booking.getBookingId(), booking.getUser().getUserID(), booking.getRoom().getRoomID(), invoice.getAmount(),
                booking.getStatus(), invoice.getPaymentStatus(), booking.getCheckIn(), booking.getCheckOut());
        System.out.println("----------------------\n");
    }

    private void cancelBooking() {
        System.out.println("Enter Booking ID: ");
        int bookingId = scanner.nextInt();
        scanner.nextLine();

        log.info("Cancellation process started for Booking ID: {}", bookingId);

        Response bookingResponse = bookingController.getBookingById(bookingId);
        if (bookingResponse == null) {
            log.warn("Booking not found for ID: {}", bookingId);
            System.out.println("\nBooking not found for: " + bookingId);
            return;
        }

        Response invoiceResponse = invoiceController.getInvoiceByBookingId(bookingId);
        Invoice invoice = (Invoice) invoiceResponse.getData();
        if (Objects.isNull(invoice)){
            System.out.println("No invoice found for this booking.");
            return;
        }
        System.out.println("Are you sure you want to cancel the booking with ID: " + bookingId + "? (Y/N)");
        String confirmation = scanner.nextLine();
        if (confirmation.equalsIgnoreCase("Y")) {
            Response cancleBookingResponse = bookingController.cancelBooking(bookingId);
            Booking booking = (Booking) cancleBookingResponse.getData();
            if (cancleBookingResponse.isSuccess()) {
                log.info("Booking ID: {} successfully canceled", bookingId);

                boolean updateSuccess = updateRoomAvailability(booking.getRoom().getRoomID(), true);
                if (updateSuccess) {
                    invoice.setPaymentStatus(PaymentStatus.CANCELED);
                    invoiceController.updatePaymentStatus(invoice.getInvoiceId(), PaymentStatus.CANCELED);
                    log.info("Room availability updated for Room ID: {} and payment status set to CANCELED", booking.getRoom().getRoomID());
                    System.out.println("Booking successfully canceled, and room availability updated.");
                } else {
                    log.error("Failed to update room availability for Room ID: {}", booking.getRoom().getRoomID());
                    System.out.println("Booking canceled, but failed to update room availability.");
                }
            } else {
                log.error("Failed to cancel Booking ID: {}", bookingId);
                System.out.println("Failed to cancel the booking.");
            }
        } else {
            log.info("Booking cancellation aborted for Booking ID: {}", bookingId);
            System.out.println("Booking cancellation aborted.");
        }
    }

    private  boolean updateRoomAvailability(int roomId, boolean isAvailable) {
        log.info("Attempting to update room availability. Room ID: {}, Availability: {}", roomId, isAvailable);
        Response roomResponse = roomController.getRoomById(roomId);
        Room room = (Room) roomResponse.getData();
        if (room != null) {
            room.setAvailable(isAvailable);
            Response updateRoomResponse = roomController.updateRoom(room);

            if (updateRoomResponse.isSuccess()) {
                log.info("Room ID: {} availability successfully updated to: {}", roomId, isAvailable);
            } else {
                log.error("Failed to update availability for Room ID: {}", roomId);
            }
            return updateRoomResponse.isSuccess();
        } else {
            log.warn("Room with ID {} not found.", roomId);
            return false;
        }
    }

    private void checkoutByStaff() {
        System.out.println("Enter User Email ID for checkout: ");
        String userEmail = scanner.nextLine();
        if (!Validator.isValidEmail(userEmail)) {
            log.warn("Invalid email format entered: {}", userEmail);
            System.out.println("Invalid email format. Please enter a valid email.");
            return;
        }

        Response userResponse = userController.getUserByEmail(userEmail);
        if (!userResponse.isSuccess()) {
            log.warn("No user found with the provided email: {}", userEmail);
            System.out.println("\nNo user found with the provided email.");
            return;
        }

        User user = (User) userResponse.getData();
        log.info("Initiating checkout for user: {} ({})", user.getName(), userEmail);

        Response bookingResponse = bookingController.getConfirmedBookingByUserId(user.getUserID());
        Booking activeBooking = (Booking) bookingResponse.getData();
        if (!bookingResponse.isSuccess()) {
            log.warn("No active confirmed booking found for user: {}", userEmail);
            System.out.println("\nNo active confirmed booking found for user: " + userEmail);
            return;
        }
        Response invoiceResponse = invoiceController.getInvoiceByBookingId(activeBooking.getBookingId());
        Invoice invoice = (Invoice) invoiceResponse.getData();
        if (Objects.isNull(invoice)){
            System.out.println("\nInvoice is not available for this booking.");
            return;
        }
        if (invoice.getPaymentStatus().equals(PaymentStatus.PENDING)) {
            log.info("Pending payment detected for booking ID: {}", activeBooking.getBookingId());
            while (true) {
                System.out.print("Is payment collected? (yes/no): ");
                String paymentCollected = scanner.nextLine().trim().toLowerCase();

                if (paymentCollected.equals("yes")) {
                    invoice.setPaymentStatus(PaymentStatus.PAID);
                    activeBooking.setStatus(BookingStatus.COMPLETED);
                    activeBooking.setCheckOut(LocalDateTime.now());
                    log.info("Payment collected and status updated to 'PAID' for booking ID: {}", activeBooking.getBookingId());
                    System.out.println("Payment status updated to 'PAID'.");
                    break;
                } else if (paymentCollected.equals("no")) {
                    log.warn("Payment not collected for booking ID: {}. Checkout aborted.", activeBooking.getBookingId());
                    System.out.println("Please collect the payment before proceeding.");
                } else {
                    log.warn("Invalid payment input received: {}", paymentCollected);
                    System.out.println("Invalid input. Please enter 'yes' or 'no'.");
                }
            }
        }

        activeBooking.setStatus(BookingStatus.COMPLETED);
        bookingController.updateBooking(activeBooking);
        log.info("Booking ID: {} status updated to COMPLETED", activeBooking.getBookingId());

        Response roomResponse = roomController.getRoomById(activeBooking.getRoom().getRoomID());
        Room bookedRoom = (Room) roomResponse.getData();
        if (bookedRoom != null) {
            bookedRoom.setAvailable(true);
            roomController.updateRoom(bookedRoom);
            log.info("Room {} is now available after checkout", bookedRoom.getRoomNumber());
            System.out.println("Room " + bookedRoom.getRoomNumber() + " is now available.");
        } else {
            log.error("Associated room not found for booking ID: {}", activeBooking.getBookingId());
            System.out.println("Associated room not found.");
        }

        log.info("Checkout completed successfully for user: {}", user.getName());
        System.out.println("Checkout completed successfully for " + user.getName() + ".");
    }

    private void bookRoomByStaff() {
        log.info("Booking room for a user.");
        System.out.print("Enter user email: ");
        String email = scanner.nextLine();
        if (!Validator.isValidEmail(email)) {
            log.warn("Invalid email format entered: {}", email);
            System.out.println("Invalid email format. Please enter a valid email.");
            return;
        }

        Response userResponse = userController.getUserByEmail(email);
        User user = (User) userResponse.getData();
        if (!userResponse.isSuccess()) {
            log.info("User not found, creating a new user.");
            user = createNewUser(email);
        }
        if(Objects.isNull(user)){
            System.out.println("Unable to create user! Please try after sometime.");
            return;
        }
        System.out.println("User found: " + user.getName() + " (" + user.getEmail() + ")");

        Response roomResponse = roomController.getAvailableRooms();
        List<Room> availableRooms = (List<Room>) roomResponse.getData();
        if (availableRooms.isEmpty()) {
            System.out.println("No available rooms at the moment.");
            return;
        }
        displayAvailableRooms(availableRooms);

        Room selectedRoom = getRoomSelection(availableRooms);
        if (selectedRoom == null) {
            System.out.println("Operation canceled, the room you've selected might have booked by other already.");
            log.warn("Operation canceled, the room you've selected might have booked by other already.");
            return;
        }

        Booking newBooking = createBooking(user, selectedRoom);
        finalizeBooking(newBooking, selectedRoom);
        log.info("Room booked successfully for user: {}", user.getEmail());
    }

    private void finalizeBooking(Booking booking, Room room) {
        bookingController.createBooking(booking);
        room.setAvailable(false);
        roomController.updateRoom(room);
        System.out.printf("Booking confirmed! Room: %d, Check-in: %s, Check-out: %s, Amount: Rs.%.2f%n",
                room.getRoomID(), booking.getCheckIn(), booking.getCheckOut(), room.getPrice() * Duration.between(booking.getCheckIn(), booking.getCheckOut()).toDays());
    }

    private Booking createBooking(User user, Room room) {
        System.out.print("Enter check-in date (YYYY-MM-DD HH:MM) or press Enter for today's date: ");
        String checkInInput = scanner.nextLine().trim();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime checkIn = checkInInput.isEmpty() ? LocalDateTime.now().withHour(12).withMinute(0) : LocalDateTime.parse(checkInInput, formatter);

        System.out.print("Enter duration (in days): ");
        int duration = scanner.nextInt();
        scanner.nextLine();
        LocalDateTime checkOut = checkIn.plusDays(duration);

        log.info("Creating booking for user {} in room {} from {} to {}", user.getEmail(), room.getRoomNumber(), checkIn, checkOut);
        return new Booking(user, room, checkIn, checkOut, BookingStatus.CONFIRMED);

    }

    private Room getRoomSelection(List<Room> availableRooms) {
        System.out.print("\nEnter Room ID to book: ");
        int roomId = scanner.nextInt();
        scanner.nextLine();

        return availableRooms.stream()
                .filter(room -> room.getRoomID() == roomId)
                .findFirst()
                .orElse(null);
    }

    private void displayAvailableRooms(List<Room> rooms) {
        System.out.println("\nAvailable Rooms:");
        rooms.forEach(room -> System.out.printf("Room ID: %d | Room Number: %d | Type: %s | Price: %.2f | Available: %s%n",
                room.getRoomID(), room.getRoomNumber(), room.getRoomType(), room.getPrice(), (room.isAvailable() ? "Yes" : "No")));
    }

    private User createNewUser(String email) {
        System.out.println("User not found! Creating a new user profile.");

        System.out.print("Enter full name: ");
        String name = scanner.nextLine().toUpperCase();
        if (!Validator.isValidName(name)) {
            log.warn("Invalid name format entered: {}", name);
            System.out.println("Invalid name format. Please enter a valid name.");
            return null;
        }

        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        if (!Validator.isValidPassword(password)) {
            log.warn("Invalid password format entered: {}", password);
            System.out.println("Invalid password format. Please enter a valid password.");
            return null;
        }

        User user = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .userRole(UserRole.GUEST)
                .build();

        Response registerUserResponse = userController.registerUser(user);
        User newRegisteredUser = (User) registerUserResponse.getData();
        if (registerUserResponse.getStatus().equals(ResponseStatus.ERROR)) {
            log.error("Unable to register user.");
            System.out.println("Unable to register user");
        }
        Response responseUser = userController.getUserByEmail(email);
        User fetchNewlyRegisteredUser = (User) responseUser.getData();
        List<Guest> guests = collectGuestDetails();

        for (Guest guest : guests) {
            guest.setUser(fetchNewlyRegisteredUser);
            userController.addAccompaniedGuest(guest);// attach the guest to this GuestUser
        }
        log.info("New guest user created successfully: {}", email);
        System.out.println("🎉 Guest user created successfully!");

        return fetchNewlyRegisteredUser;
    }

    private List<Guest> collectGuestDetails() {
        List<Guest> guests = new ArrayList<>();
        System.out.print("Will the user have accompanied guests? (yes/no): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("yes")) return guests;

        System.out.print("Enter the number of guests: ");
        int guestCount = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < guestCount; i++) {
            System.out.print("Enter guest name for guest " + (i + 1) + ": ");
            String name = scanner.nextLine();
            System.out.print("Enter guest age for guest " + (i + 1) + ": ");
            int age = scanner.nextInt();
            scanner.nextLine();
            guests.add(new Guest(null, name, age, null));
        }

        log.info("Collected details for {} guest(s)", guests.size());
        return guests;
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
