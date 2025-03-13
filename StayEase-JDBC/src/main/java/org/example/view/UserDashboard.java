package org.example.view;

import org.example.constants.BookingStatus;
import org.example.constants.RoomType;
import org.example.constants.UserRole;
import org.example.entity.Booking;
import org.example.entity.Invoice;
import org.example.entity.Room;
import org.example.entity.User;
import org.example.service.BookingService;
import org.example.service.InvoiceService;
import org.example.service.RoomService;
import org.example.service.UserService;

import java.time.LocalDateTime;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class UserDashboard {

    private final UserService userService;
    private final RoomService roomService;
    private final BookingService bookingService;
    private final InvoiceService invoiceService;
    private final Scanner scanner;

    private User loggedInUser;

    public UserDashboard(UserService userService, RoomService roomService,
                         BookingService bookingService, InvoiceService invoiceService,
                         Scanner scanner) {
        this.userService = userService;
        this.roomService = roomService;
        this.bookingService = bookingService;
        this.invoiceService = invoiceService;
        this.scanner = scanner;
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    public void displayUserMenu() {
        if (loggedInUser == null) {
            System.out.println("Error: No user is logged in!");
            return;
        }

        while (true) {
            System.out.println("\n===== User Dashboard =====");
            System.out.println("Welcome, " + loggedInUser.getName() + "!");
            System.out.println("Role: " + loggedInUser.getUserRole());
            System.out.println("----------------------------");

            if (loggedInUser.getUserRole() == UserRole.GUEST) {
                System.out.println("1. View Available Rooms");
                System.out.println("2. Book a Room");
                System.out.println("3. View My Bookings");
            } else if (loggedInUser.getUserRole() == UserRole.STAFF) {
                System.out.println("1. View Available Rooms");
                System.out.println("2. Manage Rooms");
                System.out.println("3. View All Bookings");
            }

            System.out.println("4. View Invoice");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        viewAvailableRooms();
                        break;
                    case 2:
                        if (loggedInUser.getUserRole() == UserRole.GUEST) {
                            bookRoom(loggedInUser);
                        } else {
                            manageRooms();
                        }
                        break;
                    case 3:
                        if (loggedInUser.getUserRole() == UserRole.GUEST) {
                            viewUserBookings(loggedInUser.getUserID());
                        } else {
                            viewAllBookings();
                        }
                        break;
                    case 4:
//                        viewInvoice(loggedInUser.getUserID());
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
        List<Room> availableRooms = roomService.getAvailableRooms();
        if (availableRooms.isEmpty()) {
            System.out.println("No available rooms at the moment.");
        } else {
            System.out.println("Available Rooms:");
            for (Room room : availableRooms) {
                System.out.println(room);
            }
        }
    }

    private void bookRoom(User user) {
        System.out.print("Enter the room number you'd like to book: ");
        int roomNumber = scanner.nextInt();
        scanner.nextLine();

        Room room = roomService.getRoomById(roomNumber);
        if (room == null || !room.isAvailable()) {
            System.out.println("Room not available for booking.");
            return;
        }

        System.out.print("Enter check-in date (YYYY-MM-DD): ");
        String checkInDate = scanner.nextLine();
        System.out.print("Enter check-out date (YYYY-MM-DD): ");
        String checkOutDate = scanner.nextLine();

        Booking booking = new Booking();
        booking.setUserId(user.getUserID());
        booking.setRoomId(room.getRoomID());
        booking.setCheckIn(LocalDateTime.parse(checkInDate + "T00:00:00"));
        booking.setCheckOut(LocalDateTime.parse(checkOutDate + "T00:00:00"));
        booking.setStatus(BookingStatus.PENDING); // Booking needs admin approval

        bookingService.createBooking(booking);
        System.out.println("Booking created. Awaiting admin approval.");
    }

    private void manageRooms() {
        System.out.println("1. Add Room");
        System.out.println("2. Update Room");
        System.out.println("3. Mark Room Under Maintenance");
        System.out.print("Enter your choice: ");
        try {
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addRoom();
                    break;
                case 2:
                    updateRoom();
                    break;
                case 3:
                    markRoomUnderMaintenance();
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input!");
            scanner.nextLine();
        }
    }

    private void addRoom() {
        System.out.print("Enter room number: ");
        int roomNumber = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter room type (SINGLE/DOUBLE/SUITE): ");
        String typeInput = scanner.nextLine().toUpperCase();

        RoomType roomType;
        try {
            roomType = RoomType.valueOf(typeInput);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid room type.");
            return;
        }

        System.out.print("Enter price: ");
        double price = scanner.nextDouble();
        scanner.nextLine();

        Room room = new Room();
        room.setRoomNumber(roomNumber);
        room.setRoomType(roomType);
        room.setPrice(price);
        room.setAvailable(true);

        roomService.addRoom(room);
        System.out.println("Room added successfully.");
    }

    private void updateRoom() {
        // Similar to addRoom, but calls roomService.updateRoom(...)
    }

    private void markRoomUnderMaintenance() {
        System.out.print("Enter room ID to mark under maintenance: ");
        int roomId = scanner.nextInt();
        scanner.nextLine();
        roomService.markRoomUnderMaintenance(roomId);
        System.out.println("Room marked under maintenance.");
    }

    private void viewUserBookings(int userId) {
        List<Booking> bookings = bookingService.getBookingsByUser(userId);
        if (bookings.isEmpty()) {
            System.out.println("You have no bookings.");
        } else {
            System.out.println("Your Bookings:");
            bookings.forEach(System.out::println);
        }
    }

    private void viewAllBookings() {
        List<Booking> allBookings = bookingService.getBookingsByUser(-1); // Get all bookings
        allBookings.forEach(System.out::println);
    }

//    private void viewInvoice(int userId) {
//        Invoice invoice = invoiceService.getInvoiceByUserId(userId);
//        if (invoice == null) {
//            System.out.println("No invoice found for this user.");
//        } else {
//            System.out.println("Invoice: " + invoice);
//        }
//    }
}

