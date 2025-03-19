package org.example.view;

import org.example.constants.BookingStatus;
import org.example.constants.PaymentStatus;
import org.example.constants.UserRole;
import org.example.controller.BookingController;
import org.example.controller.InvoiceController;
import org.example.controller.RoomController;
import org.example.controller.UserController;
import org.example.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Menu {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Logger log = LoggerFactory.getLogger(Menu.class);

    private final RoomController roomController;
    private final UserController userController;
    private final BookingController bookingController;
    private final InvoiceController invoiceController;

    private final AdminDashBoard adminDashBoard;

    public Menu(RoomController roomController, UserController userController, BookingController bookingController, InvoiceController invoiceController, AdminDashBoard adminDashBoard) {
        this.roomController = roomController;
        this.userController = userController;
        this.bookingController = bookingController;
        this.invoiceController = invoiceController;
        this.adminDashBoard = new AdminDashBoard(roomController, userController, bookingController, invoiceController, scanner);
    }

    public void displayMainMenu() {
        while (1>0) {
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
        List<Room> availableRooms = roomController.getAvailableRooms();
        if (availableRooms.isEmpty()) {
            System.out.println("\nNo available rooms found.");
        } else {
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
        System.out.print("Enter your email: ");
        String email = scanner.nextLine().toLowerCase();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();
        System.out.print("Enter Role (STAFF/GUEST): ");
        String roleInput = scanner.nextLine().toUpperCase();

        try {
            UserRole role = UserRole.valueOf(roleInput);

            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            user.setUserRole(role);

            if (userController.isEmailExists(email)) {
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

    private void loginUser() { // TODO generic abstract UI
        System.out.print("\nEnter email: ");
        String email = scanner.nextLine().toLowerCase();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        boolean isAuthenticated = userController.authenticateUser(email, password);
        if (isAuthenticated) {
            User user = userController.getUserByEmail(email);

            if (user.getUserRole() != UserRole.SUPER_ADMIN && ! user.isActive()) {
                System.out.println(user.getName() + ", Your account is currently inactive. Please contact administrator for further assistance.");
            } else {
                switch (user.getUserRole()) {
                    case STAFF:
                        System.out.println("\nLogin successful! Welcome Mr." + user.getName() + " (" + user.getUserRole() + ")");
                        displayStaffMenu(user);
                        break;
                    case GUEST:
                        System.out.println("\nLogin successful! Welcome Mr." + user.getName() + " (" + user.getUserRole() + ")");
                        displayUserMenu(user);
                        break;
                    case ADMIN:
                        System.out.println("\nLogin successful! Welcome Mr." + user.getName() + " (" + user.getUserRole() + ")");
                        adminDashBoard.displayAdminMenu(user);
                        break;
                    case SUPER_ADMIN:
                        System.out.println("\nLogin successful! Welcome Mr." + user.getName() + " (" + user.getUserRole() + ")");
                        adminDashBoard.displaySuperAdminMenu(user);
                        break;
                    default:
                        System.out.println("Unknown user role.");
                        break;
                }
            }
        } else {
            System.out.println("Invalid credentials. Please try again.");
        }
    }

    private void displayStaffMenu(User loggedInStaff) {
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
                    case 1:
                        searchUserDetails();
                        break;
                    case 2:
                        viewAvailableRooms();
                        break;
                    case 3:
                        bookRoomByStaff();
                        break;
                    case 4:
                        checkoutByStaff();
                        break;
                    case 5:
                        cancelBooking();
                        break;
                    case 6:
                        generateInvoiceByBookingId();
                        break;
                    case 7:
                        System.out.println("Logging out...");
                        return;
                    case 8:
                        System.out.println("Exiting...");
                        System.exit(0);
                    default:
                        System.out.println("Invalid Choice! Please Choose Between 1 to 8 Only.");

                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number between 1 and 8.");
                scanner.nextLine();
            }
        }
    }

    public void checkoutByStaff() {
        System.out.println("\nEnter User Email ID for checkout:");
        String userEmail = scanner.nextLine();

        User user = userController.getUserByEmail(userEmail);
        if (user == null) {
            System.out.println("\nNo user found with the provided email.");
            return;
        }

        Booking activeBooking = bookingController.getConfirmedBookingByUserId(user.getUserID());
        if (activeBooking == null || !activeBooking.getStatus().equals(BookingStatus.CONFIRMED)) {
            System.out.println("----------------------------------");
            return;
        }
        Invoice invoice = invoiceController.getInvoiceByBookingId(activeBooking.getBookingId());
        if(invoice.getPaymentStatus().equals(PaymentStatus.PENDING)) {
            System.out.println("\nPayment is pending for this booking.");
            while (true) {
                System.out.print("Is payment collected? (yes/no): ");
                String paymentCollected = scanner.nextLine().trim().toLowerCase();
                if (paymentCollected.equals("yes")) {
                    invoice.setPaymentStatus(PaymentStatus.PAID);
                    activeBooking.setStatus(BookingStatus.COMPLETED);
                    activeBooking.setCheckOut(LocalDateTime.now());
                    System.out.println("Payment status updated to 'PAID'.");
                    break;
                } else if (paymentCollected.equals("no")) {
                    System.out.println("Please collect the payment before proceeding.");
                } else {
                    System.out.println("Invalid input. Please enter 'yes' or 'no'.");
                }
            }
        }
        activeBooking.setStatus(BookingStatus.COMPLETED);
        bookingController.updateBooking(activeBooking);

        Room bookedRoom = roomController.getRoomById(activeBooking.getRoomId());
        if (bookedRoom != null) {
            bookedRoom.setAvailable(true);
            roomController.updateRoom(bookedRoom);
            System.out.println("Room " + bookedRoom.getRoomNumber() + " is now available.");
        } else {
            System.out.println("Associated room not found.");
        }
        System.out.println("Checkout completed successfully for " + user.getName() + ".");
    }

    public void cancelBooking() {
        System.out.println("Enter Booking ID: ");
        int bookingId = scanner.nextInt();
        scanner.nextLine();

        Invoice invoice = invoiceController.getInvoiceByBookingId(bookingId);
        Booking booking = bookingController.getBookingById(bookingId);
        if (booking == null) {
            System.out.println("\nBooking not found for :"+ bookingId);
            return;
        }
        System.out.println("Are you sure you want to cancel the booking with ID: " + bookingId + "? (Y/N)");
        String confirmation = scanner.nextLine();
        if (confirmation.equalsIgnoreCase("Y")) {
            boolean cancellationSuccess = bookingController.cancelBooking(bookingId);
            if (cancellationSuccess) {
                boolean updateSuccess = updateRoomAvailability(booking.getRoomId(), true);
                if (updateSuccess) {
                    invoice.setPaymentStatus(PaymentStatus.CANCELED);
                    invoiceController.updatePaymentStatus(invoice.getInvoiceId(),PaymentStatus.CANCELED);
                    System.out.println("Booking successfully canceled, and room availability updated.");
                } else {
                    System.out.println("Booking canceled, but failed to update room availability.");
                }
            } else {
                System.out.println("Failed to cancel the booking.");
            }
        } else {
            System.out.println("Booking cancellation aborted.");
        }
    }

    public void cancelBookingByUser(User user) {
        List<Booking> allBookings = bookingController.getBookingsByUser(user.getUserID());
        List<Booking> eligibleBookings = allBookings.stream()
                .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED || booking.getStatus() == BookingStatus.PENDING)
                .toList();

        if (eligibleBookings.isEmpty()) {
            System.out.println("No bookings available for cancellation.");
            return;
        }
        System.out.println("Your eligible bookings for cancellation:");
        for (int i = 0; i < eligibleBookings.size(); i++) {
            Booking booking = eligibleBookings.get(i);
            System.out.println((i + 1) + ". Booking ID: " + booking.getBookingId() +
                    ", Status: " + booking.getStatus() +
                    ", Room Number: " + booking.getRoomId() +
                    ", Check-in Date: " + booking.getCheckIn() +
                    ", Check-out Date: " + booking.getCheckOut());
        }

        System.out.print("Enter the number of the booking you wish to cancel: ");
        Scanner scanner = new Scanner(System.in);
        int selection;
        try {
            selection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Cancellation aborted.");
            return;
        }

        if (selection < 1 || selection > eligibleBookings.size()) {
            System.out.println("Invalid selection. Cancellation aborted.");
            return;
        }

        Booking bookingToCancel = eligibleBookings.get(selection - 1);

        System.out.println("Are you sure you want to cancel the booking with ID: " + bookingToCancel.getBookingId() + "? (Y/N)");
        String confirmation = scanner.nextLine();
        if (confirmation.equalsIgnoreCase("Y")) {
            boolean cancellationSuccess = bookingController.cancelBooking(bookingToCancel.getBookingId());
            if (cancellationSuccess) {
                boolean updateSuccess = updateRoomAvailability(bookingToCancel.getRoomId(), true);
                if (updateSuccess) {
                    System.out.println("Booking successfully canceled, and room availability updated.");
                } else {
                    System.out.println("Booking canceled, but failed to update room availability.");
                }
            } else {
                System.out.println("Failed to cancel the booking.");
            }
        } else {
            System.out.println("Booking cancellation aborted.");
        }
    }

    public boolean updateRoomAvailability(int roomId, boolean isAvailable) {
        Room room = roomController.getRoomById(roomId);
        if (room != null) {
            room.setAvailable(isAvailable);
            return roomController.updateRoom(room);
        } else {
            System.out.println("Room with ID " + roomId + " not found.");
            return false;
        }
    }

    public void generateInvoiceByBookingId() {
        System.out.println("Enter Booking ID to generate invoice:");
        int bookingId = scanner.nextInt();
        Invoice invoice = invoiceController.getInvoiceByBookingId(bookingId);

        if (invoice == null) {
            System.out.println("Invoice not found for the given Booking ID.");
            return;
        }
        Booking booking = bookingController.getBookingById(bookingId);

        if (booking == null) {
            System.out.println("Booking not found.");
            return;
        }
        long daysBetween = Duration.between(booking.getCheckIn(), booking.getCheckOut()).toDays();
        if (daysBetween <= 0) {
            System.out.println("Invalid check-in and check-out dates.");
            return;
        }

        System.out.println("\n------ INVOICE ------");
        System.out.println("Booking ID: " + booking.getBookingId());
        System.out.println("User Email: " + booking.getUserId());
        System.out.println("Room Number: " + booking.getRoomId());
        System.out.println("Total amount: " + invoice.getAmount());
        System.out.println("Booking Status: " + booking.getStatus());
        System.out.println("Payment Status: " + invoice.getPaymentStatus());
        System.out.println("Check-in Date: " + booking.getCheckIn());
        System.out.println("Check-out Date: " + booking.getCheckOut());
        System.out.println("----------------------\n");
    }

    private void bookRoomByStaff() {
        System.out.print("Enter user email: ");
        String email = scanner.nextLine();

        User user = userController.getUserByEmail(email);
        if (user == null) {
            System.out.println("User not found! Creating a new user profile...");

            System.out.print("Enter full name: ");
            String name = scanner.nextLine();

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            boolean isActive = true;
            List<Guest> accompaniedGuests = new ArrayList<>();
            System.out.print("Will the user have accompanied guests? (yes/no): ");
            String hasGuests = scanner.nextLine().trim().toLowerCase();
            if (hasGuests.equals("yes")) {
                System.out.print("Enter the number of guests: ");
                int guestCount = scanner.nextInt();
                scanner.nextLine();
                for (int i = 0; i < guestCount; i++) {
                    System.out.print("Enter guest name: ");
                    String guestName = scanner.nextLine();
                    System.out.print("Enter guest age: ");
                    int guestAge = scanner.nextInt();
                    scanner.nextLine();
                    accompaniedGuests.add(new Guest(0, guestName, guestAge, 0)); // guestID & userID will be assigned later
                }
            }

            user = new GuestUser(0, name, email, password, isActive, accompaniedGuests);
            int newUserId = userController.createUser(user);
            user.setUserID(newUserId);

            if (user instanceof GuestUser) {
                for (Guest guest : ((GuestUser) user).getAccompaniedGuests()) {
                    guest.setUserId(newUserId);
                    userController.addAccompaniedGuest(guest);
                }
            }

            System.out.println("New user created successfully!");
        }

        System.out.println("User found: " + user.getName() + " (" + user.getEmail() + ")");

        List<Room> availableRooms = roomController.getAvailableRooms();
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
        Booking newBooking = new Booking(0, user.getUserID(), roomId, checkInDate, checkOutDate, BookingStatus.CONFIRMED);
        bookingController.createBooking(newBooking);
        selectedRoom.setAvailable(false);
        roomController.updateRoom(selectedRoom);
        scheduleRoomAvailabilityReset(roomId, checkOutDate);

        System.out.println("\n============================================");
        System.out.println("Booking confirmed for " + user.getName() + "!");
        System.out.println("=============================================");
        System.out.printf("Room ID       : %d%n", roomId);
        System.out.printf("Check-In Date : %s%n", checkInDate);
        System.out.printf("Check-Out Date: %s%n", checkOutDate);
        System.out.printf("Total Amount  : Rs.%.2f%n", totalAmount);
        System.out.printf("Booking Status: %s%n", newBooking.getStatus());
        System.out.println("---------------------------------------------");

        boolean isPaid = bookingPaymentChoice();
        int generatedInvoiceId = generateInvoiceAtBooking(newBooking.getBookingId(), user.getUserID(), totalAmount, isPaid);
        Invoice invoice = invoiceController.getInvoiceById(generatedInvoiceId);
        if (invoice != null) {
            System.out.println("\nInvoice generated successfully for " + user.getName());
            System.out.println(invoice);
        } else {
            System.out.println("Invoice generation failed.");
        }
    }

    private void bookRoomByUser(User loggedInUser) {
        List<Room> availableRooms = roomController.getAvailableRooms();
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
        Invoice invoice = invoiceController.getInvoiceById(generatedInvoiceId);
        if (invoice != null) {
            System.out.println("\nInvoice generated successfully for " + loggedInUser.getName());
            System.out.println(invoice);
        } else {
            System.out.println("Invoice generation failed.");
        }
    }

    private void scheduleRoomAvailabilityReset(int roomId, LocalDateTime checkOutDate) {
        long delay = Duration.between(LocalDateTime.now(), checkOutDate).toMillis();
//        ExecutorService servuce = Executors.newSingleThread(1);
//        Future<?> future = service.submit(() -> {
//
//        });

//        event base approach
//        future.cancel(True);


        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            Room room = roomController.getRoomById(roomId);
            if (room != null) {
                room.setAvailable(true);
                roomController.updateRoom(room);
                System.out.println("Room ID " + roomId + " is now available again.");
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

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
        System.out.println("\nEnter user email: ");
        String email = scanner.nextLine();

        User user = userController.getUserByEmail(email);
        if (user != null) {
            System.out.println("==================================");
            System.out.println("             User Found           ");
            System.out.println("==================================");
            System.out.println("Name: " + user.getName());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Role: " + user.getUserRole());
            System.out.println("==================================");
            List<Booking> bookings = bookingController.getBookingsByUser(user.getUserID());
            if (bookings.isEmpty()) {
                System.out.println("No bookings found for this user.");
            } else {
                System.out.println("           Booking History        ");
                System.out.println("----------------------------------");
                for (Booking booking : bookings) {
                    System.out.println("Booking ID: " + booking.getBookingId());
                    System.out.println("Date: " + booking.getCheckIn());
                    System.out.println("Date: " + booking.getCheckOut());
                    System.out.println("Status: " + booking.getStatus());
                    System.out.println("----------------------------------");
                }
            }
        } else {
            System.out.println("User not found.");
        }
    }

    private void displayUserMenu(User loggedInGuest) {
        while (true) {
            System.out.println("\n====== User Menu ======");
            System.out.println("Welcome, " + loggedInGuest.getName() + "!");
            System.out.println("Role: " + loggedInGuest.getUserRole());
            System.out.println("=========================");
            System.out.println("1. Book a Room");
            System.out.println("2. View My Bookings");
            System.out.println("3. Cancel My Booking");
            System.out.println("4. View Booking Invoice");
            System.out.println("5. Logout");
            System.out.println("-------------------------");
            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        bookRoomByUser(loggedInGuest);
                        break;
                    case 2:
                        viewBooking(loggedInGuest);
                        break;
                    case 3:
                        cancelBookingByUser(loggedInGuest);
                        break;
                    case 4:
                        viewInvoice(loggedInGuest);
                        break;
                    case 5:
                        System.out.println("Logging out...");
                        return;
                    default:
                        System.out.println("Invalid choice! Please enter a number between 1 and 5.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number between 1 and 5.");
                scanner.nextLine();
            }
        }
    }

    private void viewInvoice(User loggedInGuest) {
        List<Invoice> invoices = invoiceController.getInvoiceByUserId(loggedInGuest.getUserID());
        if (invoices.isEmpty()) {
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
            }
        }
    }

    private void viewBooking(User loggedInGuest) {
        List<Booking> bookings = bookingController.getBookingsByUser(loggedInGuest.getUserID());
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
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
            }
        }
    }

    private int generateInvoiceAtBooking(int bookingId, int userId, double totalAmount, boolean isPaid) {

        PaymentStatus paymentStatus = isPaid ? PaymentStatus.PAID : PaymentStatus.PENDING;
        return invoiceController.generateInvoice(new Invoice(0, bookingId, userId, totalAmount, LocalDateTime.now(), paymentStatus));
    }
}
