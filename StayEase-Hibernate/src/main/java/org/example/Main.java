package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import lombok.extern.log4j.Log4j2;

import org.example.consoleinterface.HomeMenuUI;
import org.example.dependency.DependencyInjector;
import org.example.persistence.CustomPersistenceUnitInfo;

import org.hibernate.jpa.HibernatePersistenceProvider;


import java.util.HashMap;
import java.util.Map;

@Log4j2
public class Main {
    public static void main(String[] args) {

        HomeMenuUI homeMenuUI = DependencyInjector.initHomeMenuUI();
        homeMenuUI.displayMainMenu();
    }
}


//        Map<String, String> properties = new HashMap<>();
//        properties.put("hibernate.show_sql", "true");
//        properties.put("hibernate.hbm2ddl.auto", "create");
//
//        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-persistence-unit")) {
//
//        try (EntityManagerFactory emf = new HibernatePersistenceProvider()
//                .createContainerEntityManagerFactory(new CustomPersistenceUnitInfo(), properties)) {
//
//            try (EntityManager entityManager = emf.createEntityManager()) {
//                entityManager.getTransaction().begin();
//
//                Persons p = new Persons();
//                p.setName("Shubham");
//
//                Passport passport = new Passport();
//                passport.setNumber("SDK214JIT");
//
//                p.setPassport(passport);
//
//                entityManager.persist(p);
//                entityManager.persist(passport);
//                entityManager.getTransaction().commit();
//            }
//        }
//    }
//}
