package org.example.consoleinterface;

import lombok.extern.log4j.Log4j2;
import org.example.constants.BookingStatus;
import org.example.constants.PaymentStatus;
import org.example.constants.UserRole;
import org.example.controller.BookingController;
import org.example.controller.InvoiceController;
import org.example.controller.RoomController;
import org.example.controller.UserController;
import org.example.entity.*;
import org.example.utility.MenuHandler;
import org.example.utility.PrintGenericResponse;
import org.example.utility.Response;
import org.example.utility.Validator;
import org.example.wrapperclass.RoomAvailabilityResult;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.example.constants.ResponseStatus.ERROR;

@Log4j2
public class UserMenuUI {
    private final UserController userController;
    private final RoomController roomController;
    private final BookingController bookingController;
    private final InvoiceController invoiceController;

    private final PrintGenericResponse printGenericResponse;
    private final MenuHandler menuHandler;
    private final Scanner scanner;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Future<?> scheduledTask;
    DateTimeFormatter showDateInFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm a");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public UserMenuUI(UserController userController, RoomController roomController, BookingController bookingController, InvoiceController invoiceController, PrintGenericResponse printGenericResponse, MenuHandler menuHandler, Scanner scanner) {
        this.userController = userController;
        this.roomController = roomController;
        this.bookingController = bookingController;
        this.invoiceController = invoiceController;
        this.printGenericResponse = printGenericResponse;
        this.menuHandler = menuHandler;
        this.scanner = scanner;
    }

    public void displayUserMenu(User loggedInGuest) {
        int choice;
        System.out.println("\nWelcome, " + loggedInGuest.getName() + "!" + "\nRole: " + loggedInGuest.getUserRole());

        do {
            menuHandler.displayMenu("User Menu", new String[]{"Book a Room", "View My Bookings", "Cancel My Booking", "View Booking Invoice", "Logout"});
            try {
                choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> {
                        log.info("User {} is booking a room", loggedInGuest.getUserID());
                        System.out.println("Freely book the available room.");
                        bookRoomByUser(loggedInGuest);
                    }
                    case 2 -> {
                        log.info("User {} is viewing their bookings", loggedInGuest.getUserID());
                        viewBooking(loggedInGuest);
                    }
                    case 3 -> {
                        log.info("User {} is canceling a booking", loggedInGuest.getUserID());
                        cancelBookingByUser(loggedInGuest);
                    }
                    case 4 -> {
                        log.info("User {} is viewing their invoice", loggedInGuest.getUserID());
                        viewInvoice(loggedInGuest);
                    }
                    case 5 -> {
                        log.info("User {} is logging out", loggedInGuest.getUserID());
                        System.out.println("User is logged out successfully.");
                    }
                    default -> {
                        System.out.println("Invalid menu choice.");
                        log.warn("Invalid menu choice {} by user {}", choice, loggedInGuest.getUserID());
                    }
                }
            } catch (InputMismatchException e) {
                log.error("Invalid input by user {}: {}", loggedInGuest.getUserID(), e.getMessage());
                System.out.println("Invalid input! Please enter a number from above options.");
                scanner.nextLine();
                choice = -1;
            }
        } while (choice != 5);
    }

    public void registerUser() {
        String name, email, password, roleInput;
        UserRole role;

        while (true) {
            System.out.print("\nEnter your name (or type 0 to cancel): ");
            name = scanner.nextLine().trim();
            if (name.equals("0")) return;

            if (!Validator.isValidName(name)) {
                log.warn("Invalid name format entered: {}", name);
                System.out.println("❌ Invalid name format. Please enter a valid name.");
            } else {
                name = name.toUpperCase();
                break;
            }
        }
        while (true) {
            System.out.print("Enter your email (or type 0 to cancel): ");
            email = scanner.nextLine().trim();
            if (email.equals("0")) return;

            if (!Validator.isValidEmail(email)) {
                log.warn("Invalid email format entered: {}", email);
                System.out.println("❌ Invalid email format. Please enter a valid email.");
            } else {
                email = email.toLowerCase();
                break;
            }
        }
        while (true) {
            System.out.print("Enter your password (or type 0 to cancel): ");
            password = scanner.nextLine().trim();
            if (password.equals("0")) return;

            if (!Validator.isValidPassword(password)) {
                log.warn("Invalid password format entered for email: {}", email);
                System.out.println("❌ Invalid password. Use at least 1 lowercase, 1 uppercase, 1 digit, 1 special char, and min 4 chars.");
            } else {
                break;
            }
        }
        while (true) {
            System.out.print("Enter User Role: \n1.STAFF \n2.GUEST \n(or type 0 to cancel): ");
            String input = scanner.nextLine().trim();

            if (input.matches("[12]")) {
                role = input.equals("1") ? UserRole.STAFF : UserRole.GUEST;
                break;
            } else if (input.equals("0")) {
                return;
            } else {
                System.out.println("❌ Invalid input! Please enter 1 for STAFF or 2 for GUEST.");
            }
        }

        try {
            Response userResponse = userController.isEmailExists(email);
            if (userResponse.getData().equals(true)) {
                System.out.println("❗ This email is already registered. Please use a different email.");
                return;
            }

            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            user.setUserRole(role);

            if (role == UserRole.GUEST) {
                user.setActive(true);
            } else {
                user.setActive(false);
                System.out.println("✅ Staff registration request submitted! Awaiting admin approval.\n");
                System.out.println("\n🎉 Congratulations " + user.getName() + " (" + user.getUserRole() + ")" + "! You can login after your account gets active by admin.");
                return;
            }
            Response registerUserResponse = userController.registerUser(user);

            if (registerUserResponse.getStatus().equals(ERROR)) {
                log.error("Unable to register user.");
                System.out.println("Unable to register user");
                return;
            }
            System.out.println("\n🎉 Congratulations " + user.getName() + " (" + user.getUserRole() + ")" + "! You can now log in.\n");

        } catch (Exception e) {
            log.error("Unexpected error occurred during registration.", e);
        }
    }

    public RoomAvailabilityResult viewAvailableRooms() {
        List<Room> availableRooms = new ArrayList<>();
        LocalDateTime checkInDate = null;
        LocalDateTime checkOutDate = null;

        while (true) {
            try {
                System.out.print("Enter Check-in Date (dd-MM-yyyy) or 0 to cancel: ");
                String checkInInput = scanner.nextLine();
                if (checkInInput.equals("0")) {
                    System.out.println("Operation cancelled.");
                    return null;
                }

                LocalDate parsedCheckIn = LocalDate.parse(checkInInput, formatter);
                checkInDate = parsedCheckIn.atStartOfDay();

                LocalDate today = LocalDate.now();
                if (parsedCheckIn.isBefore(today) || parsedCheckIn.isAfter(today.plusDays(60))) {
                    System.out.println("Check-in date must be from today to the next 60 days: " +
                            formatter.format(today) + " to " + formatter.format(today.plusDays(60)));
                    continue;
                }

                break;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use dd-MM-yyyy.");
            }
        }

        while (true) {
            try {
                System.out.print("Enter Check-out Date (dd-MM-yyyy) or 0 to cancel: ");
                String checkOutInput = scanner.nextLine();
                if (checkOutInput.equals("0")) {
                    System.out.println("Operation cancelled.");
                    return null;
                }

                LocalDate parsedCheckOut = LocalDate.parse(checkOutInput, formatter);
                checkOutDate = parsedCheckOut.atStartOfDay();

                if (!checkOutDate.isAfter(checkInDate)) {
                    System.out.println("Check-out date must be after check-in date.");
                    continue;
                }

                long stayDuration = java.time.Duration.between(checkInDate, checkOutDate).toDays();
                if (stayDuration > 180) {
                    System.out.println("Stay duration cannot exceed 180 days. You selected: " + stayDuration + " days.");
                    continue;
                }

                break;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use dd-MM-yyyy.");
            }
        }

        Response roomResponse = roomController.getAvailableRoomsForDate(checkInDate, checkOutDate);
        Object data = roomResponse.getData();

        if (data instanceof List<?>) {
            for (Object obj : (List<?>) data) {
                if (obj instanceof Room room) {
                    availableRooms.add(room);
                }
            }
        }

        if (availableRooms.isEmpty()) {
            System.out.println("\nAll rooms are currently booked. Please check back later.");
        } else {
            List<String> ignore = List.of("isAvailable");
            printGenericResponse.printTable(availableRooms, ignore);
        }

        return new RoomAvailabilityResult(availableRooms, checkInDate, checkOutDate);
    }


    private void viewInvoice(User loggedInGuest) {
        log.info("User {} requested to view their invoices.", loggedInGuest.getUserID());

        Response invoiceResponse = invoiceController.getInvoiceByUserId(loggedInGuest.getUserID());
        List<Invoice> invoices = (List<Invoice>) invoiceResponse.getData();
        if (Objects.isNull(invoices) || invoices.isEmpty()) {
            System.out.println("\nNo invoices found for user " + loggedInGuest.getName());
            log.warn("No invoices found for user {}.", loggedInGuest.getUserID());
        } else {
            System.out.println("\n==========================");
            System.out.println("      Invoice History     ");
            System.out.println("==========================");

            for (Invoice invoice : invoices) {
                System.out.println("Invoice NO: " + "INV-" + invoice.getInvoiceId());
                System.out.println("Booking NO: " + "BOK-" + invoice.getBooking().getBookingId());
                System.out.println("Date: " + invoice.getIssueDate().format(showDateInFormat));
                System.out.println("Amount: " + invoice.getAmount());
                System.out.println("Payment Status: " + invoice.getPaymentStatus());
                System.out.println("--------------------------");

                log.info("Displayed invoice: ID {} | Booking ID {} | Amount: {} | Payment Status: {}",
                        invoice.getInvoiceId(), invoice.getBooking().getBookingId(), invoice.getAmount(), invoice.getPaymentStatus());
            }
        }
    }

    private void cancelBookingByUser(User user) {
        Response bookingResponse = bookingController.getBookingsByUser(user.getUserID());
        Object data = bookingResponse.getData();
        List<Booking> allBookings = new ArrayList<>();

        if (data instanceof List<?>) {
            for (Object obj : (List<?>) data) {
                if (obj instanceof Booking) {
                    allBookings.add((Booking) obj);
                }
            }
        }

        if (allBookings.isEmpty()) {
            System.out.println("\nNo Bookings found for ".concat(user.getName()).concat(" at moment!"));
            log.warn("No bookings found for {} at moment!", user.getName());
            return;
        }

        List<Booking> eligibleBookings = allBookings.stream()
                .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED
                        || booking.getStatus() == BookingStatus.PENDING)
                .toList();

        if (eligibleBookings.isEmpty()) {
            log.info("No bookings available for cancellation.");
            System.out.println("No bookings available for cancellation.");
            return;
        }

        displayEligibleBookings(eligibleBookings);

        int bookingId = getValidBookingSelection(eligibleBookings);
        if (bookingId == -1) return; // Invalid input, so exit.

        Booking bookingToCancel = eligibleBookings.stream()
                .filter(b -> b.getBookingId() == bookingId)
                .findFirst()
                .orElse(null);

        if (bookingToCancel == null) {
            log.error("Unexpected error: booking ID {} not found after validation.", bookingId);
            System.out.println("Unexpected error occurred. Try again.");
            return;
        }

        Response invoiceResponse = invoiceController.getInvoiceByBookingId(bookingToCancel.getBookingId());
        Invoice invoice = (Invoice) invoiceResponse.getData();
        if (confirmCancellation(bookingToCancel.getBookingId())) {
            if (processBookingCancellation(bookingToCancel)) {
                if (!Objects.isNull(invoice)) {
                    invoice.setPaymentStatus(PaymentStatus.CANCELED);
                    invoiceController.updatePaymentStatus(invoice.getInvoiceId(), PaymentStatus.CANCELED);
                }
                log.info("Booking successfully canceled, and room availability updated.");
                System.out.println("Booking successfully canceled, and room availability updated.");
            } else {
                log.warn("Booking canceled, but failed to update room availability.");
                System.out.println("Booking canceled, but failed to update room availability.");
            }
        } else {
            log.info("Booking cancellation aborted.");
            System.out.println("Booking cancellation aborted.");
        }
    }

    private boolean confirmCancellation(int bookingId) {
        log.info("Are you sure you want to cancel the booking with ID: {}? (Y/N)", bookingId);
        System.out.println("Are you sure you want to cancel the booking? (Y/N)");
        String confirmation = scanner.nextLine().trim();
        return confirmation.equalsIgnoreCase("Y");
    }

    private boolean processBookingCancellation(Booking booking) {
        Response cancelBookingResponse = bookingController.cancelBooking(booking.getBookingId());
        if (!cancelBookingResponse.isSuccess()) {
            log.error("Failed to cancel the booking with ID: {}", booking.getBookingId());
            return false;
        }
        boolean updateSuccess = updateRoomAvailability(booking.getRoom().getRoomID(), true);
        if (!updateSuccess) {
            log.warn("Booking ID: {} canceled, but failed to update room availability for Room ID: {}.",
                    booking.getBookingId(), booking.getRoom().getRoomID());
        }
        return updateSuccess;
    }

    private boolean updateRoomAvailability(int roomId, boolean isAvailable) {
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

    private int getValidBookingSelection(List<Booking> eligibleBookings) {
        System.out.println("\nEnter the booking ID you wish to cancel:");
        log.info("Prompting user to enter booking ID to cancel.");

        try {
            int bookingId = Integer.parseInt(scanner.nextLine());

            boolean bookingExists = eligibleBookings.stream()
                    .anyMatch(booking -> booking.getBookingId() == bookingId);

            if (!bookingExists) {
                log.warn("Booking ID {} not found in eligible bookings.", bookingId);
                System.out.println("Invalid booking ID. Cancellation aborted.");
                return -1;
            }
            return bookingId;
        } catch (NumberFormatException e) {
            log.error("Invalid input for booking ID.", e);
            System.out.println("Invalid input. Please enter a numeric booking ID.");
            return -1;
        }
    }

    private void displayEligibleBookings(List<Booking> bookings) {
        System.out.println("\nEligible Bookings for Cancellation:");
        System.out.println("=====================================================================================");
        System.out.printf("%-5s %-12s %-10s %-12s %-20s %-20s%n",
                "No.", "Booking ID", "Status", "Room No.", "Check-In", "Check-Out");
        System.out.println("-------------------------------------------------------------------------------------");

        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);
            System.out.printf("%-5d %-12d %-10s %-12d %-20s %-20s%n",
                    (i + 1),
                    booking.getBookingId(),
                    booking.getStatus(),
                    booking.getRoom().getRoomID(),
                    booking.getCheckIn().format(showDateInFormat),
                    booking.getCheckOut().format(showDateInFormat));
            log.info("{}. Booking ID: {}, Status: {}, Room Number: {}, Check-in: {}, Check-out: {}",
                    (i + 1), booking.getBookingId(), booking.getStatus(), booking.getRoom().getRoomID(),
                    booking.getCheckIn(), booking.getCheckOut());
        }
        System.out.println("=====================================================================================\n");
    }

    private void viewBooking(User loggedInGuest) {
        log.info("User {} requested to view their bookings.", loggedInGuest.getUserID());

        Response bookingResponse = bookingController.getBookingsByUser(loggedInGuest.getUserID());
        Object data = bookingResponse.getData();
        List<Booking> bookings = new ArrayList<>();

        if (data instanceof List<?>) {
            for (Object obj : (List<?>) data) {
                if (obj instanceof Booking booking) {
                    bookings.add(booking);
                }
            }
        }

        if (bookings.isEmpty()) {
            log.warn("No bookings found for {}.", loggedInGuest.getName());
            System.out.println("\nNo bookings found for " + loggedInGuest.getName());
        } else {
            System.out.println("\n=========================================");
            System.out.println("             Booking History             ");
            System.out.println("=========================================");

            for (Booking booking : bookings) {
//                printGenericResponse.printTable(bookings, null);
                System.out.printf("Booking ID: %s%nDate: %s%nDate: %s%nStatus: %s%n-----------------------------------------%n",
                        booking.getBookingId(), booking.getCheckIn().format(showDateInFormat), booking.getCheckOut().format(showDateInFormat), booking.getStatus());

                log.info("Displayed booking: ID {} | Check-in: {} | Check-out: {} | Status: {}",
                        booking.getBookingId(), booking.getCheckIn(), booking.getCheckOut(), booking.getStatus());
            }
        }
    }

    private void bookRoomByUser(User loggedInUser) {
        RoomAvailabilityResult result = viewAvailableRooms();
        if (result == null || result.getAvailableRooms().isEmpty()) {
            return;
        }

        List<Room> availableRooms = result.getAvailableRooms();
        LocalDateTime checkInDate = result.getCheckInDate().withHour(10).withMinute(0);
        LocalDateTime checkOutDate = result.getCheckOutDate().withHour(11).withMinute(0);

        int roomId;
        Room selectedRoom = null;

        while (true) {
            System.out.print("\nEnter Room ID to book (or 0 to cancel): ");
            while (!scanner.hasNextInt()) {
                System.out.print("Please enter a valid number: ");
                scanner.next(); // consume invalid input
            }
            roomId = scanner.nextInt();
            scanner.nextLine();

            if (roomId == 0) {
                System.out.println("Booking cancelled.");
                return;
            }

            int finalRoomId = roomId;
            selectedRoom = availableRooms.stream()
                    .filter(room -> room.getRoomID() == finalRoomId)
                    .findFirst()
                    .orElse(null);

            if (selectedRoom != null) {
                break;
            } else {
                System.out.println("Invalid Room Number. Please select a valid room.");
            }
        }

        List<Guest> guests = collectGuestDetails();
        for (Guest guest : guests) {
            guest.setUser(loggedInUser);
            userController.addAccompaniedGuest(guest);
        }

        int duration = (int) ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        checkOutDate = checkInDate.plusDays(duration);

        double totalAmount = selectedRoom.getPrice() * duration;
        Booking newBooking = new Booking(loggedInUser, selectedRoom, checkInDate, checkOutDate, BookingStatus.CONFIRMED);
        bookingController.createBooking(newBooking);

        roomController.updateRoom(selectedRoom);
        scheduleRoomAvailabilityReset(roomId, checkOutDate);

        boolean isPaid = bookingPaymentChoice();
        int generatedInvoiceId = generateInvoiceAtBooking(newBooking.getBookingId(), loggedInUser.getUserID(), totalAmount, isPaid);
        Response invoiceResponse = invoiceController.getInvoiceById(generatedInvoiceId);
        Invoice invoice = (Invoice) invoiceResponse.getData();

        if (invoice != null) {
            System.out.println("\nInvoice generated successfully for " + loggedInUser.getName());
            System.out.println("\nBooking confirmed for " + loggedInUser.getName() + "!");
            System.out.println(invoice);
            printGenericResponse.printInvoice(newBooking, invoice);
        } else {
            System.out.println("Invoice generation failed.");
        }
    }

    private List<Guest> collectGuestDetails() {
        List<Guest> guests = new ArrayList<>();

        while (true) {
            System.out.print("Will the user have accompanied guests?\n1. Yes\n2. No\n0. Cancel\nEnter option: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            if (option == 0 || option == 2) return guests;
            if (option != 1) {
                System.out.println("Invalid option. Try again.");
                continue;
            }

            int guestCount = -1;
            while (true) {
                System.out.print("Enter number of guests (1–16) OR (0 to cancel): ");
                guestCount = scanner.nextInt();
                scanner.nextLine();
                if (guestCount == 0) return guests;
                if (guestCount > 0 && guestCount <= 16) break;

                System.out.println("Guest count must be between 1 and 16.");
            }

            for (int i = 1; i <= guestCount; i++) {
                String name;
                while (true) {
                    System.out.print("Enter guest name for guest " + i + " (or 0 to cancel): ");
                    name = scanner.nextLine().toUpperCase();
                    if (name.equals("0")) return guests;
                    if (Validator.isValidName(name)) break;

                    log.warn("Invalid name format: {}", name);
                    System.out.println("Invalid name format. Try again.");
                }

                int age;
                while (true) {
                    System.out.print("Enter guest age for guest " + i + " (1–100, 0 to cancel): ");
                    age = scanner.nextInt();
                    scanner.nextLine();
                    if (age == 0) return guests;
                    if (age > 0 && age <= 100) break;

                    System.out.println("Invalid age. Try again.");
                }
                guests.add(new Guest(null, name, age, null));
            }
            log.info("Collected details for {} guest(s)", guests.size());
            return guests;
        }
    }


    private int generateInvoiceAtBooking(int bookingId, int userId, double totalAmount, boolean isPaid) {
        log.info("Generating invoice for user {} | Booking ID: {} | Amount: {} | Paid: {}",
                userId, bookingId, totalAmount, isPaid);

        Response bookingResponse = bookingController.getBookingById(bookingId);
        Booking booking = (Booking) bookingResponse.getData();
        Response userResponse = userController.getUserById(userId);
        User user = (User) userResponse.getData();

        PaymentStatus paymentStatus = isPaid ? PaymentStatus.PAID : PaymentStatus.PENDING;
        Response invoiceResponse = invoiceController.generateInvoice(new Invoice(null, booking, user, totalAmount, LocalDateTime.now(), paymentStatus));
        Integer invoiceId = (Integer) invoiceResponse.getData();
        if (invoiceId > 0) {
            log.info("Invoice successfully generated: ID {}", invoiceId);
        } else {
            log.error("Invoice generation failed for user {} and booking ID {}", userId, bookingId);
        }

        return invoiceId;
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

    //Best use case: When cancellation/modification of the task might be required.
    private void scheduleRoomAvailabilityReset(int roomId, LocalDateTime checkOutDate) {
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
}
