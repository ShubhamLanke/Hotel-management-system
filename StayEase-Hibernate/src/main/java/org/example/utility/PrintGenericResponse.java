package org.example.utility;

import org.example.entity.Booking;
import org.example.entity.Invoice;
import org.example.entity.Room;
import org.example.entity.User;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

public class PrintGenericResponse {

    public <T> void printTable(List<T> list, List<String> ignoreFields) {
        if (list == null || list.isEmpty()) return;

        Class<?> clazz = list.get(0).getClass();
        Field[] allFields = clazz.getDeclaredFields();
        List<Field> fields = Arrays.stream(allFields)
                .filter(f -> ignoreFields == null || !ignoreFields.contains(f.getName()))
                .peek(f -> f.setAccessible(true))
                .toList();

        int totalWidth = fields.size() * 17;
        String line = new String(new char[totalWidth]).replace('\0', '=');
        String lineEnd = new String(new char[totalWidth]).replace('\0', '-');

        System.out.println(line);
        for (Field field : fields) {
            System.out.printf("%-17s", field.getName());
        }
        System.out.println();
        System.out.println(line);

        for (T item : list) {
            for (Field field : fields) {
                try {
                    Object value = field.get(item);
                    if (field.getName().equalsIgnoreCase("price")) {
                        System.out.printf("Rs.%-14.2f", Double.parseDouble(value.toString()));
                    } else {
                        System.out.printf("%-17s", value);
                    }
                } catch (Exception e) {
                    System.out.printf("%-17s", "N/A");
                }
            }
            System.out.println();
        }

        System.out.println(lineEnd);
    }

    public void printInvoice(Booking booking, Invoice invoice) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        long totalNights = (ChronoUnit.DAYS.between(booking.getCheckIn().toLocalDate(), booking.getCheckOut().toLocalDate()));

        System.out.println("==================================================");
        System.out.printf("%30s%n", "HOTEL STAY EASE INVOICE");
        System.out.println("==================================================");

        System.out.printf("Invoice No   : %s%n", "INV-" + booking.getBookingId());
        System.out.printf("Date         : %s%n", LocalDateTime.now().format(dtf));
        System.out.println("--------------------------------------------------");

        User guest = booking.getUser();
        System.out.printf("Guest Name   : %s%n", guest.getName());
        System.out.printf("Email        : %s%n", guest.getEmail());
//        System.out.printf("Contact No   : %s%n", guest.getPhone());
        System.out.println("--------------------------------------------------");

        System.out.println("Room Details");
        System.out.println("--------------------------------------------------");

        Room room = booking.getRoom();
        System.out.printf("Room No      : %s%n", room.getRoomNumber());
        System.out.printf("Room Type    : %s%n", room.getRoomType());
        System.out.printf("Price/Night  : ₹ %.2f%n", room.getPrice());
        System.out.printf("Check-in     : %s%n", booking.getCheckIn().format(dtf));
        System.out.printf("Check-out    : %s%n", booking.getCheckOut().format(dtf));

        System.out.printf("Total Nights : %d%n", totalNights);
        System.out.println("--------------------------------------------------");

        double subtotal = room.getPrice() * totalNights;
        double tax = subtotal * 0.12;
        double total = subtotal + tax;

        System.out.println("Amount Summary");
        System.out.println("--------------------------------------------------");
        System.out.printf("Subtotal     : ₹ %.2f%n", subtotal);
        System.out.printf("Tax (12%%)    : ₹ %.2f%n", tax);
        System.out.printf("Total Amount : ₹ %.2f%n", total);
        System.out.printf("Payment status: %s%n", invoice.getPaymentStatus());

        System.out.println("==================================================");
        System.out.printf("%30s%n", "Thank you for choosing Our Hotel!");
        System.out.println("==================================================");
    }
}
