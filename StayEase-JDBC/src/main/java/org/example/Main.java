package org.example;

import org.example.controller.BookingController;
import org.example.controller.InvoiceController;
import org.example.controller.RoomController;
import org.example.controller.UserController;
import org.example.dao.BookingDaoImpl;
import org.example.dao.InvoiceDaoImpl;
import org.example.dao.RoomDaoImpl;
import org.example.dao.UserDaoImpl;
import org.example.service.*;
import org.example.view.AdminDashBoard;
import org.example.view.Menu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        RoomService roomService = new RoomServiceImpl(new RoomDaoImpl());
        UserService userService = new UserServiceImpl(new UserDaoImpl());
        BookingService bookingService = new BookingServiceImpl(new BookingDaoImpl());
        InvoiceService invoiceService = new InvoiceServiceImpl(new InvoiceDaoImpl());

        RoomController roomController = new RoomController(roomService);
        UserController userController = new UserController(userService);
        BookingController bookingController = new BookingController(bookingService);
        InvoiceController invoiceController = new InvoiceController(invoiceService);

        AdminDashBoard adminDashBoard = new AdminDashBoard( roomController, userController, bookingController, invoiceController, scanner);

        Menu menu = new Menu(roomController, userController, bookingController, invoiceController, adminDashBoard);
        menu.displayMainMenu();

    }
}
