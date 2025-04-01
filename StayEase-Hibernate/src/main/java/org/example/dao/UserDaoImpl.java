package org.example.dao;

import lombok.extern.log4j.Log4j2;
import org.example.entity.Guest;
import org.example.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

@Log4j2
public class UserDaoImpl implements UserDao {
    private final SessionFactory sessionFactory;

    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void registerUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
        } catch (Exception e) {
            log.error("Error while registering user: " + e.getMessage());
            throw new RuntimeException("Email is already registered!");
        }
    }

    @Override
    public Optional<User> loginUser(String email, String password) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE email = :email AND password = :password AND isActive = true", User.class);
            query.setParameter("email", email);
            query.setParameter("password", password);
            User user = query.uniqueResult();
            return Optional.ofNullable(user);
        } catch (Exception e) {
            log.error("Error during login: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<User> getAllStaff() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User WHERE userRole = 'STAFF'", User.class).list();
        }
    }

    @Override
    public List<User> getAllAdmins() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User WHERE userRole = 'ADMIN'", User.class).list();
        }
    }

    @Override
    public void approveStaff(int userId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            User user = session.get(User.class, userId);
            if (user != null) {
                user.setActive(true);
                session.update(user);
            }
            transaction.commit();
        }
    }

    @Override
    public Optional<User> getUserById(int userId) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(User.class, userId));
        }
    }

    @Override
    public Optional<User> getUserByEmailId(String emailId) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
            query.setParameter("email", emailId);
            return Optional.ofNullable(query.uniqueResult());
        }
    }

    @Override
    public boolean isEmailExists(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(*) FROM User WHERE email = :email", Long.class);
            query.setParameter("email", email);
            return query.uniqueResult() > 0;
        }
    }

    @Override
    public int createUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            int userId = (int) session.save(user);
            transaction.commit();
            return userId;
        }
    }

    @Override
    public void updateUserToInactive(User user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            user.setActive(false);
            session.update(user);
            transaction.commit();
        }
    }

    @Override
    public void updateUserToActive(User user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            user.setActive(true);
            session.update(user);
            transaction.commit();
        }
    }

    @Override
    public void addAccompaniedGuest(Guest guest) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(guest);
            transaction.commit();
        }
    }
}