package org.example.view;

import org.example.consoleinterface.RecordHandler;
import org.example.constants.BookingStatus;
import org.example.constants.PaymentStatus;
import org.example.constants.UserRole;
import org.example.controller.BookingController;
import org.example.controller.InvoiceController;
import org.example.controller.RoomController;
import org.example.controller.UserController;
import org.example.entity.*;
import org.example.utility.Response;
import org.example.utility.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.example.constants.ResponseStatus.ERROR;

public class Menu {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Logger log = LoggerFactory.getLogger(Menu.class);

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Future<?> scheduledTask;

    private final RoomController roomController;
    private final UserController userController;
    private final BookingController bookingController;
    private final InvoiceController invoiceController;

    private final AdminDashBoard adminDashBoard;

    private final RecordHandler recordHandler;

    public Menu(RoomController roomController, UserController userController, BookingController bookingController, InvoiceController invoiceController, AdminDashBoard adminDashBoard, RecordHandler recordHandler) {
        this.roomController = roomController;
        this.userController = userController;
        this.bookingController = bookingController;
        this.invoiceController = invoiceController;
        this.recordHandler = recordHandler;
        this.adminDashBoard = new AdminDashBoard(roomController, userController, bookingController, invoiceController);
    }

    public void displayMainMenu() {
        while (1 > 0) {
            System.out.println("\n==============================");
            System.out.println("  Welcome to StayEase Hotel!");
            System.out.println("==============================");
            System.out.println("1. View Available Rooms");
            System.out.println("2. Register User");
            System.out.println("3. Login");
            System.out.println("------------------------------");
            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        viewAvailableRooms();
                        break;
                    case 2:
                        registerUser();
                        break;
                    case 3:
                        loginUser();
                        break;
                    default:
                        log.info("Invalid choice! Please enter a number between 1 and 4.");
                }
            } catch (InputMismatchException e) {
                log.error("Invalid input! Please enter a number between 1 and 4.");
                scanner.nextLine();
            }
        }
    }

    private void viewAvailableRooms() {
        Response roomResponse = roomController.getAvailableRooms();
        List<Room> availableRooms = (List<Room>) roomResponse.getData();
        if (roomResponse.getStatus().equals(ERROR)) {
            System.out.println("\nNo available rooms found.");
        } else {
            recordHandler.printTable(availableRooms,"Available Rooms");
            System.out.println("\n=======================================================");
            System.out.printf("%-10s %-15s %-15s %-10s %n",
                    "Room ID", "Room Number", "Room Type", "Price");
            System.out.println("=======================================================");
            for (Room room : availableRooms) {
                System.out.printf("%-10d %-15d %-15s Rs.%-9.2f %n",
                        room.getRoomID(),
                        room.getRoomNumber(),
                        room.getRoomType().toString(),
                        room.getPrice());
            }
            System.out.println("-------------------------------------------------------");
        }
    }

    private void registerUser() {
        System.out.print("\nEnter your name: ");
        String name = scanner.nextLine().toUpperCase();
        if (!Validator.isValidName(name)) {
            log.warn("Invalid name format entered: {}", name);
            System.out.println("Invalid name format. Please enter a valid name.");
            return;
        }
        System.out.print("Enter your email: ");
        String email = scanner.nextLine().toLowerCase();
        if (!Validator.isValidEmail(email)) {
            log.warn("Invalid email format entered: {}", email);
            System.out.println("Invalid email format. Please enter a valid email.");
            return;
        }
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();
        if (!Validator.isValidPassword(password)) {
            log.warn("Invalid password format entered: {}", email);
            System.out.println("Invalid password format. Please enter a valid password with At least 1 lowercase, 1 uppercase, 1 digit, 1 special, and 4+ chars.");
            return;
        }
        System.out.print("Enter Role (STAFF/GUEST): ");
        String roleInput = scanner.nextLine().toUpperCase();

        try {
            UserRole role = UserRole.valueOf(roleInput);

            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            user.setUserRole(role);
            Response userResponse = userController.isEmailExists(email);
            if (!userResponse.isSuccess()) {
                System.out.println("Error: This email is already registered. Please use a different email.");
                return;
            }
            if (role == UserRole.GUEST) {
                user.setActive(true);
                System.out.println("Guest registered successfully!");
            } else {
                user.setActive(false);
                System.out.println("Staff registration request submitted! Awaiting admin approval.");
            }

            userController.registerUser(user);
            System.out.println("\nCongratulations " + user.getName() + "! You can log in now!");
            System.out.println("User Type: " + user.getUserRole());

        } catch (IllegalArgumentException e) {
            System.out.println("Invalid role! Please enter either STAFF or GUEST.");
        }
    }

    private void loginUser() {
        System.out.print("\nEnter email: ");
        String email = scanner.nextLine().toLowerCase();
        if (!Validator.isValidEmail(email)) {
            log.warn("Invalid email format entered: {}", email);
            System.out.println("Invalid email format. Please enter a valid email.");
            return;
        }
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        Response authenticateUser = userController.authenticateUser(email, password);
        if (authenticateUser.isSuccess()) {
            Response userResponse = userController.getUserByEmail(email);

            if (userResponse.isSuccess()) {
                User user = (User) userResponse.getData();
                if (user.getUserRole() != UserRole.SUPER_ADMIN && !user.isActive()) {
                    log.warn("Inactive account login attempt: {}", email);
                    System.out.println(user.getName() + ", your account is currently inactive. Please contact the administrator.");
                    return;
                }
                log.info("Login successful: {} ({})", user.getName(), user.getUserRole());

                switch (user.getUserRole()) {
                    case STAFF -> displayStaffMenu(user);
                    case GUEST -> displayUserMenu(user);
                    case ADMIN -> adminDashBoard.displayAdminMenu(user);
                    case SUPER_ADMIN -> adminDashBoard.displaySuperAdminMenu(user);
                    default -> log.error("Unknown user role for user: {}", email);
                }
            } else {
                log.warn("User not found: {}", email);
                System.out.println("User not found. Please try again.");
            }
        } else {
            log.warn("Invalid login attempt for email: {}", email);
            System.out.println("\nInvalid credentials. Please try again.");
        }
    }

    private void displayStaffMenu(User loggedInStaff) {
        log.info("Staff menu accessed by: {} (Role: {})", loggedInStaff.getName(), loggedInStaff.getUserRole());
        if (Boolean.FALSE.equals(loggedInStaff.getUserRole().equals(UserRole.STAFF))) {
            System.out.println("Invalid role type!");
            log.error("Invalid role type!");
            return;
        }
        while (true) {
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
                        viewAvailableRooms();
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

    public void checkoutByStaff() {
        System.out.println("\nEnter User Email ID for checkout:");
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
            System.out.println("----------------------------------");
            return;
        }
        Response invoiceResponse = invoiceController.getInvoiceByBookingId(activeBooking.getBookingId());
        Invoice invoice = (Invoice) invoiceResponse.getData();
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

        Response roomResponse = roomController.getRoomById(activeBooking.getRoomId());
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

    public void cancelBooking() {
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

        System.out.println("Are you sure you want to cancel the booking with ID: " + bookingId + "? (Y/N)");
        String confirmation = scanner.nextLine();
        if (confirmation.equalsIgnoreCase("Y")) {
            Response cancleBookingResponse = bookingController.cancelBooking(bookingId);
            Booking booking = (Booking) cancleBookingResponse.getData();
            if (cancleBookingResponse.isSuccess()) {
                log.info("Booking ID: {} successfully canceled", bookingId);

                boolean updateSuccess = updateRoomAvailability(booking.getRoomId(), true);
                if (updateSuccess) {
                    invoice.setPaymentStatus(PaymentStatus.CANCELED);
                    invoiceController.updatePaymentStatus(invoice.getInvoiceId(), PaymentStatus.CANCELED);
                    log.info("Room availability updated for Room ID: {} and payment status set to CANCELED", booking.getRoomId());
                    System.out.println("Booking successfully canceled, and room availability updated.");
                } else {
                    log.error("Failed to update room availability for Room ID: {}", booking.getRoomId());
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

    public void cancelBookingByUser(User user) {
        Response bookingResponse = bookingController.getBookingsByUser(user.getUserID());
        List<Booking> allBookings = (List<Booking>) bookingResponse.getData();
        List<Booking> eligibleBookings = allBookings.stream()
                .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED || booking.getStatus() == BookingStatus.PENDING)
                .toList();

        if (eligibleBookings.isEmpty()) {
            log.info("No bookings available for cancellation.");
            return;
        }

        displayEligibleBookings(eligibleBookings);

        int selection = getValidBookingSelection(eligibleBookings.size());
        if (selection == -1) return; // Invalid input, so exit.

        Booking bookingToCancel = eligibleBookings.get(selection - 1);

        if (confirmCancellation(bookingToCancel.getBookingId())) {
            if (processBookingCancellation(bookingToCancel)) {
                log.info("Booking successfully canceled, and room availability updated.");
            } else {
                log.warn("Booking canceled, but failed to update room availability.");
            }
        } else {
            log.info("Booking cancellation aborted.");
        }
    }

    private void displayEligibleBookings(List<Booking> bookings) {
        log.info("Your eligible bookings for cancellation:");
        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);
            log.info("{}. Booking ID: {}, Status: {}, Room Number: {}, Check-in: {}, Check-out: {}",
                    (i + 1), booking.getBookingId(), booking.getStatus(), booking.getRoomId(),
                    booking.getCheckIn(), booking.getCheckOut());
        }
    }

    private int getValidBookingSelection(int maxSelection) {
        log.info("Enter the number of the booking you wish to cancel: ");
        try {
            int selection = Integer.parseInt(scanner.nextLine());
            if (selection < 1 || selection > maxSelection) {
                log.warn("Invalid selection. Cancellation aborted.");
                return -1;
            }
            return selection;
        } catch (NumberFormatException e) {
            log.error("Invalid input. Cancellation aborted.", e);
            return -1;
        }
    }

    private boolean confirmCancellation(int bookingId) {
        log.info("Are you sure you want to cancel the booking with ID: {}? (Y/N)", bookingId);
        String confirmation = scanner.nextLine().trim();
        return confirmation.equalsIgnoreCase("Y");
    }

    private boolean processBookingCancellation(Booking booking) {
        Response cancelBookingResponse = bookingController.cancelBooking(booking.getBookingId());
        if (!cancelBookingResponse.isSuccess()) {
            log.error("Failed to cancel the booking with ID: {}", booking.getBookingId());
            return false;
        }
        boolean updateSuccess = updateRoomAvailability(booking.getRoomId(), true);
        if (!updateSuccess) {
            log.warn("Booking ID: {} canceled, but failed to update room availability for Room ID: {}.",
                    booking.getBookingId(), booking.getRoomId());
        }
        return updateSuccess;
    }

    public boolean updateRoomAvailability(int roomId, boolean isAvailable) {
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

    public void generateInvoiceByBookingId() {
        log.info("Generating invoice by booking ID.");
        System.out.print("Enter Booking ID to generate invoice: ");
        int bookingId = scanner.nextInt();

        Response invoiceResponse = invoiceController.getInvoiceByBookingId(bookingId);
        Invoice invoice = (Invoice) invoiceResponse.getData();
        if (!validateInvoice(invoice)) return;

        Response bookingResponse = bookingController.getBookingById(bookingId);
        Booking booking = (Booking) bookingResponse.getData();
        if (!bookingResponse.isSuccess()) return;

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

    private boolean validateBooking(Booking booking) {
        if (booking == null) {
            log.warn("Booking not found.");
            System.out.println("Booking not found.");
            return false;
        }
        if (Duration.between(booking.getCheckIn(), booking.getCheckOut()).toDays() <= 0) {
            log.warn("Invalid check-in and check-out dates.");
            System.out.println("Invalid check-in and check-out dates.");
            return false;
        }
        return true;
    }

    private void displayInvoice(Booking booking, Invoice invoice) {
        System.out.println("\n------ INVOICE ------");
        System.out.printf("Booking ID: %d%nUser Email: %s%nRoom Number: %d%nTotal Amount: %.2f%nBooking Status: %s%nPayment Status: %s%nCheck-in Date: %s%nCheck-out Date: %s%n",
                booking.getBookingId(), booking.getUserId(), booking.getRoomId(), invoice.getAmount(),
                booking.getStatus(), invoice.getPaymentStatus(), booking.getCheckIn(), booking.getCheckOut());
        System.out.println("----------------------\n");
    }

    public void bookRoomByStaff() {
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
            log.warn("Invalid room selection or operation canceled.");
            return;
        }

        Booking newBooking = createBooking(user, selectedRoom);
        finalizeBooking(newBooking, selectedRoom);
        log.info("Room booked successfully for user: {}", user.getEmail());
    }

    private User createNewUser(String email) {
        System.out.println("User not found! Creating a new user profile...");
        System.out.print("Enter full name: ");
        String name = scanner.nextLine();

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

        List<Guest> guests = collectGuestDetails();

        User newUser = new GuestUser(0, name, email, password, true, guests);
        Response userResponse = userController.createUser(newUser);
        User user = (User) userResponse.getData();
        newUser.setUserID(user.getUserID());

        for (Guest guest : guests) {
            guest.setUserId(user.getUserID());
            userController.addAccompaniedGuest(guest);
        }

        log.info("New user created successfully: {}", email);
        System.out.println("New user created successfully!");
        return newUser;
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
            guests.add(new Guest(0, name, age, 0));
        }

        log.info("Collected details for {} guest(s)", guests.size());
        return guests;
    }

    private void displayAvailableRooms(List<Room> rooms) {
        System.out.println("\nAvailable Rooms:");
        rooms.forEach(room -> System.out.printf("Room ID: %d | Room Number: %d | Type: %s | Price: %.2f%n",
                room.getRoomID(), room.getRoomNumber(), room.getRoomType(), room.getPrice()));
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
        return new Booking(0, user.getUserID(), room.getRoomID(), checkIn, checkOut, BookingStatus.CONFIRMED);
    }


    private void finalizeBooking(Booking booking, Room room) {
        bookingController.createBooking(booking);
        room.setAvailable(false);
        roomController.updateRoom(room);
        System.out.printf("Booking confirmed! Room: %d, Check-in: %s, Check-out: %s, Amount: Rs.%.2f%n",
                room.getRoomID(), booking.getCheckIn(), booking.getCheckOut(), room.getPrice() * Duration.between(booking.getCheckIn(), booking.getCheckOut()).toDays());
    }

    private void bookRoomByUser(User loggedInUser) {
        Response roomResponse = roomController.getAvailableRooms();
        List<Room> availableRooms = (List<Room>) roomResponse.getData();
        if (availableRooms.isEmpty()) {
            System.out.println("No available rooms at the moment.");
            return;
        }

        System.out.println("\nAvailable Rooms:");
        for (Room room : availableRooms) {
            System.out.println("Room ID: " + room.getRoomID() + " | Room Number: " + room.getRoomNumber() + " | Type: " +
                    room.getRoomType() + " | Price: " + room.getPrice() + " | Available: " + (room.isAvailable() ? "Yes" : "No"));
        }

        System.out.print("\nEnter Room ID to book: ");
        int roomId = scanner.nextInt();
        scanner.nextLine();

        boolean isRoomValid = availableRooms.stream().anyMatch(room -> room.getRoomID() == roomId);
        if (!isRoomValid) {
            System.out.println("Invalid Room Number. Please select a valid room.");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        System.out.print("Enter check-in date (YYYY-MM-DD HH:MM) or press Enter for today's date: ");
        String checkInDateString = scanner.nextLine().trim();

        LocalDateTime checkInDate;
        if (checkInDateString.isEmpty()) {
            checkInDate = LocalDateTime.now().withHour(12).withMinute(0);
        } else {
            checkInDate = LocalDateTime.parse(checkInDateString, formatter);
        }
        System.out.print("Enter duration (in days): ");
        int duration = scanner.nextInt();
        scanner.nextLine();

        LocalDateTime checkOutDate = checkInDate.plusDays(duration);

        Room selectedRoom = availableRooms.stream().filter(room -> room.getRoomID() == roomId).findFirst().orElse(null);
        if (selectedRoom == null) {
            System.out.println("Error: Room not found.");
            return;
        }

        double totalAmount = selectedRoom.getPrice() * duration;
        Booking newBooking = new Booking(0, loggedInUser.getUserID(), roomId, checkInDate, checkOutDate, BookingStatus.CONFIRMED);
        bookingController.createBooking(newBooking);
        selectedRoom.setAvailable(false);
        roomController.updateRoom(selectedRoom);
        scheduleRoomAvailabilityReset(roomId, checkOutDate);

        System.out.println("\nBooking confirmed for " + loggedInUser.getName() + "!");
        System.out.println("Room ID: " + roomId + " | Check In Date: " + checkInDate + " | Check Out Date: " + checkOutDate);
        System.out.println("Total Amount: Rs." + totalAmount);

        boolean isPaid = bookingPaymentChoice();
        int generatedInvoiceId = generateInvoiceAtBooking(newBooking.getBookingId(), loggedInUser.getUserID(), totalAmount, isPaid);
        Response invoiceResponse = invoiceController.getInvoiceById(generatedInvoiceId);
        Invoice invoice = (Invoice) invoiceResponse.getData();
        if (invoice != null) {
            System.out.println("\nInvoice generated successfully for " + loggedInUser.getName());
            System.out.println(invoice);
        } else {
            System.out.println("Invoice generation failed.");
        }
    }

    //Best use case: When cancellation/modification of the task might be required.
    public void scheduleRoomAvailabilityReset(int roomId, LocalDateTime checkOutDate) {
        long delay = Duration.between(LocalDateTime.now(), checkOutDate).toMillis();

        scheduledTask = executorService.submit(() -> {
            try {
                Thread.sleep(delay);
                Response roomResponse = roomController.getRoomById(roomId);
                Room room = (Room) roomResponse.getData();
                if (room != null) {
                    room.setAvailable(true);
                    roomController.updateRoom(room);
                    System.out.println("Room ID " + roomId + " is now available again.");
                }
            } catch (InterruptedException e) {
                System.out.println("Room availability reset was cancelled for room ID " + roomId);
                Thread.currentThread().interrupt(); // Restore interrupt status
            }
        });
    }

    //For future purpose, when you need to stop the scheduled room availability reset before it executes.
    public void cancelScheduledReset() {
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
        }
    }

//    //Best use case: Simple one-time scheduling with no need for cancellation.
//    private void scheduleRoomAvailabilityReset(int roomId, LocalDateTime checkOutDate) {
//        long delay = Duration.between(LocalDateTime.now(), checkOutDate).toMillis();
//        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
//        scheduler.schedule(() -> {
//            Response roomResponse = roomController.getRoomById(roomId);
//            Room room = (Room) roomResponse.getData();
//            if (room != null) {
//                room.setAvailable(true);
//                roomController.updateRoom(room);
//                System.out.println("Room ID " + roomId + " is now available again.");
//            }
//        }, delay, TimeUnit.MILLISECONDS);
//    }

    private boolean bookingPaymentChoice() {
        System.out.println("\nDo you want to pay now?");
        System.out.println("1. YES");
        System.out.println("2. I'LL PAY AT CHECKOUT");

        int paymentChoice;
        while (true) {
            try {
                System.out.print("Enter your choice (1 or 2): ");
                paymentChoice = Integer.parseInt(scanner.nextLine().trim());

                if (paymentChoice == 1 || paymentChoice == 2) {
                    break;
                } else {
                    System.out.println("Invalid choice! Please enter 1 for YES or 2 for NO.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter 1 for YES or 2 for NO.");
            }
        }

        return paymentChoice == 1;
    }

    public void searchUserDetails() {
        System.out.print("\nEnter user email: ");
        String email = scanner.nextLine().trim();

        if (email.isEmpty()) {
            System.out.println("Email cannot be empty. Please enter a valid email.");
            return;
        }
        Response userResponse = userController.getUserByEmail(email);
        User user = (User) userResponse.getData();
        Response bookingResponse = bookingController.getBookingsByUser(user.getUserID());
        List<Booking> booking = (List<Booking>) bookingResponse.getData();
        displayUserDetails(user);
        if (booking != null) {
            displayBookingHistory(user);
        } else {
            System.out.println("No bookings found for "+user.getName());
            log.info("No bookings found for {}",user.getName());
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
        if (bookings.isEmpty()) {
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

    private void displayUserMenu(User loggedInGuest) {
        int choice;
        do {
            printUserMenu(loggedInGuest);

            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1 -> {
                        log.info("User {} is booking a room", loggedInGuest.getName());
                        bookRoomByUser(loggedInGuest);
                    }
                    case 2 -> {
                        log.info("User {} is viewing their bookings", loggedInGuest.getName());
                        viewBooking(loggedInGuest);
                    }
                    case 3 -> {
                        log.info("User {} is canceling a booking", loggedInGuest.getName());
                        cancelBookingByUser(loggedInGuest);
                    }
                    case 4 -> {
                        log.info("User {} is viewing their invoice", loggedInGuest.getName());
                        viewInvoice(loggedInGuest);
                    }
                    case 5 -> log.info("User {} is logging out", loggedInGuest.getName());
                    default -> log.warn("Invalid menu choice {} by user {}", choice, loggedInGuest.getName());
                }
            } catch (InputMismatchException e) {
                log.error("Invalid input by user {}: {}", loggedInGuest.getName(), e.getMessage());
                System.out.println("Invalid input! Please enter a number between 1 and 5.");
                scanner.nextLine();
                choice = -1;
            }
        } while (choice != 5);
    }

    private void printUserMenu(User user) {
        System.out.println("\n====== User Menu ======");
        System.out.println("Welcome, " + user.getName() + "!");
        System.out.println("Role: " + user.getUserRole());
        System.out.println("=========================");
        System.out.println("1. Book a Room");
        System.out.println("2. View My Bookings");
        System.out.println("3. Cancel My Booking");
        System.out.println("4. View Booking Invoice");
        System.out.println("5. Logout");
        System.out.println("-------------------------");
        System.out.print("Enter your choice: ");
    }


    private void viewInvoice(User loggedInGuest) {
        log.info("User {} requested to view their invoices.", loggedInGuest.getName());

        Response invoiceResponse = invoiceController.getInvoiceByUserId(loggedInGuest.getUserID());
        List<Invoice> invoices = (List<Invoice>) invoiceResponse.getData();
        if (invoices.isEmpty()) {
            log.warn("No invoices found for user {}.", loggedInGuest.getName());
            System.out.println("No invoice found.");
        } else {
            System.out.println("\n==========================");
            System.out.println("      Invoice History     ");
            System.out.println("==========================");

            for (Invoice invoice : invoices) {
                System.out.println("Invoice ID: " + invoice.getInvoiceId());
                System.out.println("Booking ID: " + invoice.getBookingId());
                System.out.println("Date: " + invoice.getIssueDate());
                System.out.println("Amount: " + invoice.getAmount());
                System.out.println("Payment Status: " + invoice.getPaymentStatus());
                System.out.println("--------------------------");

                log.info("Displayed invoice: ID {} | Booking ID {} | Amount: {} | Payment Status: {}",
                        invoice.getInvoiceId(), invoice.getBookingId(), invoice.getAmount(), invoice.getPaymentStatus());
            }
        }
    }

    private void viewBooking(User loggedInGuest) {
        log.info("User {} requested to view their bookings.", loggedInGuest.getName());

        Response bookingResponse = bookingController.getBookingsByUser(loggedInGuest.getUserID());
        List<Booking> bookings = (List<Booking>) bookingResponse.getData();
        if (bookings.isEmpty()) {
            log.warn("No bookings found for user {}.", loggedInGuest.getName());
        } else {
            System.out.println("\n=========================================");
            System.out.println("              Booking History             ");
            System.out.println("=========================================");

            for (Booking booking : bookings) {
                System.out.println("Booking ID: " + booking.getBookingId());
                System.out.println("Date: " + booking.getCheckIn());
                System.out.println("Date: " + booking.getCheckOut());
                System.out.println("Status: " + booking.getStatus());
                System.out.println("-----------------------------------------");

                log.info("Displayed booking: ID {} | Check-in: {} | Check-out: {} | Status: {}",
                        booking.getBookingId(), booking.getCheckIn(), booking.getCheckOut(), booking.getStatus());
            }
        }
    }


    private int generateInvoiceAtBooking(int bookingId, int userId, double totalAmount, boolean isPaid) {
        log.info("Generating invoice for user {} | Booking ID: {} | Amount: {} | Paid: {}",
                userId, bookingId, totalAmount, isPaid);

        PaymentStatus paymentStatus = isPaid ? PaymentStatus.PAID : PaymentStatus.PENDING;
        Response invoiceResponse = invoiceController.generateInvoice(new Invoice(0, bookingId, userId, totalAmount, LocalDateTime.now(), paymentStatus));
        Invoice invoice = (Invoice) invoiceResponse.getData();
        if (invoice.getInvoiceId() > 0) {
            log.info("Invoice successfully generated: ID {}", invoice.getInvoiceId());
        } else {
            log.error("Invoice generation failed for user {} and booking ID {}", userId, bookingId);
        }

        return invoice.getInvoiceId();
    }

}
