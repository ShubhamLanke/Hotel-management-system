package org.example.consoleinterface;

import lombok.extern.log4j.Log4j2;
import org.example.constants.BookingStatus;
import org.example.constants.PaymentStatus;
import org.example.controller.BookingController;
import org.example.controller.InvoiceController;
import org.example.controller.RoomController;
import org.example.controller.UserController;
import org.example.entity.*;
import org.example.utility.PrintGenericResponse;
import org.example.utility.Response;
import org.example.view.Menu;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final Menu menu;
    private final Scanner scanner;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Future<?> scheduledTask;

    public UserMenuUI(UserController userController, RoomController roomController, BookingController bookingController, InvoiceController invoiceController, PrintGenericResponse printGenericResponse, MenuHandler menuHandler, Menu menu, Scanner scanner) {
        this.userController = userController;
        this.roomController = roomController;
        this.bookingController = bookingController;
        this.invoiceController = invoiceController;
        this.printGenericResponse = printGenericResponse;
        this.menuHandler = menuHandler;
        this.menu = menu;
        this.scanner = scanner;
    }

    public void displayUserMenu(User loggedInGuest) {
        int choice;
        do {
            System.out.println("\nWelcome, " + loggedInGuest.getName() + "!");
            System.out.println("Role: " + loggedInGuest.getUserRole());
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
                        System.out.println("User is logging out...");
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

    public void viewAvailableRooms() {
        Response roomResponse = roomController.getAvailableRooms();
        Object data = roomResponse.getData();
        List<Room> availableRooms = new ArrayList<>();

        if (data instanceof List<?>) {
            for (Object obj : (List<?>) data) {
                if (obj instanceof Room room) {
                    availableRooms.add(room);
                }
            }
        }
        if (roomResponse.getStatus().equals(ERROR)) {
            System.out.println("\nNo available rooms found.");
        } else {
            List<String> ignore = Arrays.asList("isAvailable");
            printGenericResponse.printTable(availableRooms, ignore);
        }
    }

    private void viewInvoice(User loggedInGuest) {
        log.info("User {} requested to view their invoices.", loggedInGuest.getUserID());

        Response invoiceResponse = invoiceController.getInvoiceByUserId(loggedInGuest.getUserID());
        List<Invoice> invoices = (List<Invoice>) invoiceResponse.getData();
        if (Objects.isNull(invoices) || invoices.isEmpty()) {
            System.out.println("\nNo invoices found for user " + loggedInGuest.getName());
            log.warn("No invoices found for user {}.", loggedInGuest.getUserID());
        } else {
            System.out.println("\n");
            printGenericResponse.printTable(invoices, null);

            System.out.println("\n==========================");
            System.out.println("      Invoice History     ");
            System.out.println("==========================");

            for (Invoice invoice : invoices) {
                System.out.println("Invoice ID: " + invoice.getInvoiceId());
                System.out.println("Booking ID: " + invoice.getBooking().getBookingId());
                System.out.println("Date: " + invoice.getIssueDate());
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
                if(!Objects.isNull(invoice)){
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
                    booking.getCheckIn(),
                    booking.getCheckOut());
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

    private void bookRoomByUser(User loggedInUser) {
        Response roomResponse = roomController.getAvailableRooms();
        Object data = roomResponse.getData();
        List<Room> availableRooms = new ArrayList<>();

        if (data instanceof List<?>) {
            for (Object obj : (List<?>) data) {
                if (obj instanceof Room room) {
                    availableRooms.add(room);
                }
            }
        }

        if (availableRooms.isEmpty()) {
            System.out.println("No available rooms at the moment.");
            return;
        }

        System.out.println("\nAvailable Rooms:");
        for (Room room : availableRooms) {
            System.out.println("Room ID: " + room.getRoomID() + " | Room Number: " + room.getRoomNumber() + " | Type: " +
                    room.getRoomType() + " | Price: " + room.getPrice());
        }

        System.out.print("\nEnter Room ID to book: ");
        int roomId = scanner.nextInt();
        scanner.nextLine();

        boolean isRoomValid = availableRooms.stream().anyMatch(room -> room.getRoomID() == roomId);
        if (!isRoomValid) {
            System.out.println("Invalid Room Number. Please select a valid room.");
            return;
        }

        List<Guest> guests = collectGuestDetails();

        for (Guest guest : guests) {
            guest.setUser(loggedInUser);
            userController.addAccompaniedGuest(guest);
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

        int duration;
        while (true) {
            System.out.print("Enter duration (in days): ");
            duration = scanner.nextInt();
            scanner.nextLine();
            if (duration > 0) {
                break;
            } else {
                System.out.println("The duration must be greater than 0.");
            }
        }

        LocalDateTime checkOutDate = checkInDate.plusDays(duration);

        Room selectedRoom = availableRooms.stream().filter(room -> room.getRoomID() == roomId).findFirst().orElse(null);
        if (selectedRoom == null) {
            System.out.println("Room you're trying to book might have booked by someone else!");
            return;
        }

        double totalAmount = selectedRoom.getPrice() * duration;
        Booking newBooking = new Booking(loggedInUser, selectedRoom, checkInDate, checkOutDate, BookingStatus.CONFIRMED);
        bookingController.createBooking(newBooking);
        selectedRoom.setAvailable(false);
        roomController.updateRoom(selectedRoom);
        scheduleRoomAvailabilityReset(roomId, checkOutDate);

        System.out.println("Room ID: " + roomId + " | Check In Date: " + checkInDate + " | Check Out Date: " + checkOutDate);
        System.out.println("Total Amount: Rs." + totalAmount);

        boolean isPaid = bookingPaymentChoice();
        int generatedInvoiceId = generateInvoiceAtBooking(newBooking.getBookingId(), loggedInUser.getUserID(), totalAmount, isPaid);
        Response invoiceResponse = invoiceController.getInvoiceById(generatedInvoiceId);
        Invoice invoice = (Invoice) invoiceResponse.getData();
        if (invoice != null) {
            System.out.println("\nInvoice generated successfully for " + loggedInUser.getName());
            System.out.println("\nBooking confirmed for " + loggedInUser.getName() + "!");
            System.out.println(invoice);
        } else {
            System.out.println("Invoice generation failed.");
        }
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
