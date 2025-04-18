package org.example.dependency;

import org.example.consoleinterface.*;
import org.example.controller.BookingController;
import org.example.controller.InvoiceController;
import org.example.controller.RoomController;
import org.example.controller.UserController;
import org.example.dao.BookingDaoImpl;
import org.example.dao.InvoiceDaoImpl;
import org.example.dao.RoomDaoImpl;
import org.example.dao.UserDaoImpl;
import org.example.service.*;
import org.example.utility.PrintGenericResponse;

import java.util.Scanner;

public class DependencyInjector {

    private DependencyInjector() {
        throw new UnsupportedOperationException("DependencyInjector class - cannot be instantiated");
    }

    public static HomeMenuUI initHomeMenuUI() {
        Scanner scanner = new Scanner(System.in);
        MenuHandler menuHandler = new MenuHandler(scanner);
        PrintGenericResponse printGenericResponse = new PrintGenericResponse();

        UserService userService = new UserServiceImpl(new UserDaoImpl());
        RoomService roomService = new RoomServiceImpl(new RoomDaoImpl());
        BookingService bookingService = new BookingServiceImpl(new BookingDaoImpl(), userService);
        InvoiceService invoiceService = new InvoiceServiceImpl(new InvoiceDaoImpl(), userService, bookingService);

        UserController userController = new UserController(userService);
        RoomController roomController = new RoomController(roomService);
        BookingController bookingController = new BookingController(bookingService);
        InvoiceController invoiceController = new InvoiceController(invoiceService);

//        Menu menu = new Menu(roomController, userController, bookingController, invoiceController, adminDashBoard, menuHandler);
        UserMenuUI userMenuUI = new UserMenuUI(userController, roomController, bookingController, invoiceController, printGenericResponse, menuHandler, scanner);
        StaffMenuUI staffMenuUI = new StaffMenuUI(userMenuUI, userController, roomController, bookingController, invoiceController, printGenericResponse, menuHandler, scanner);
        AdminMenuUI adminMenuUI = new AdminMenuUI(menuHandler, roomController, userController, bookingController, invoiceController, scanner);
        return new HomeMenuUI(userController, adminMenuUI, userMenuUI, staffMenuUI, menuHandler,scanner);
    }
}
