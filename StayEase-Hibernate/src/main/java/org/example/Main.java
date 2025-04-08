package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.extern.log4j.Log4j2;
import org.example.persistence.CustomPersistenceUnitInfo;
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
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Log4j2
public class Main {
    public static void main(String[] args) {
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

        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.hbm2ddl.auto", "create");

//        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-persistence-unit")) {

        try (EntityManagerFactory emf = new HibernatePersistenceProvider()
                .createContainerEntityManagerFactory(new CustomPersistenceUnitInfo(), properties)) {

            try (EntityManager entityManager = emf.createEntityManager()) {
                entityManager.getTransaction().begin();

                Persons p = new Persons();
                p.setName("Shubham");

                Passport passport = new Passport();
                passport.setNumber("SDK214JIT");

                p.setPassport(passport);

                entityManager.persist(p);
                entityManager.persist(passport);
                entityManager.getTransaction().commit();
            }
        }
    }
}
