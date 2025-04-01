package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;


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
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {


        EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-persistence-unit");
        EntityManager entityManager = emf.createEntityManager();;
        try {
            entityManager.getTransaction().begin();

            Product product =new Product();
            product.setId(101);
            product.setName("Smartphone");

            entityManager.persist(product);
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }

//        RoomService roomService = new RoomServiceImpl(new RoomDaoImpl());
//        UserService userService = new UserServiceImpl(new UserDaoImpl());
//        BookingService bookingService = new BookingServiceImpl(new BookingDaoImpl(), userService);
//        InvoiceService invoiceService = new InvoiceServiceImpl(new InvoiceDaoImpl());
//
//        RoomController roomController = new RoomController(roomService);
//        UserController userController = new UserController(userService);
//        BookingController bookingController = new BookingController(bookingService);
//        InvoiceController invoiceController = new InvoiceController(invoiceService);
//
//        AdminDashBoard adminDashBoard = new AdminDashBoard( roomController, userController, bookingController, invoiceController);
//
//        Menu menu = new Menu(roomController, userController, bookingController, invoiceController, adminDashBoard);
//        menu.displayMainMenu();

    }
}