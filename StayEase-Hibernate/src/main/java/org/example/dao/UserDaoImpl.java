package org.example.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.example.entity.Guest;
import org.example.entity.User;
import org.example.persistence.PersistenceManager;

import java.util.List;
import java.util.Optional;

@Log4j2
public class UserDaoImpl implements UserDao {

    @Override
    @Transactional
    public void registerUser(User user) {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            entityManager.getTransaction().begin();
            entityManager.persist(user);
            entityManager.getTransaction().commit();
            log.info("User registered successfully: {}", user);
        } catch (Exception e) {
            log.error("Error while registering user: {}", e.getMessage(), e);
            throw new RuntimeException("Email is already registered!");
        }
    }

    @Override
    public Optional<User> loginUser(String email, String password) {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            TypedQuery<User> query = entityManager.createQuery(
                    "FROM User WHERE email = :email AND password = :password AND isActive = true", User.class);
            query.setParameter("email", email);
            query.setParameter("password", password);
            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            log.error("Error during login: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public List<User> getAllStaff() {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            return entityManager.createQuery("FROM User WHERE userRole = 'STAFF'", User.class).getResultList();
        }
    }

    @Override
    public List<User> getAllAdmins() {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            return entityManager.createQuery("FROM User WHERE userRole = 'ADMIN'", User.class).getResultList();
        }
    }

    @Override
    @Transactional
    public void approveStaff(int userId) {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            entityManager.getTransaction().begin();
            User user = entityManager.find(User.class, userId);
            if (user != null) {
                user.setActive(true);
                entityManager.merge(user);
                entityManager.getTransaction().commit();
                log.info("User with ID {} approved as staff.", userId);
            }
        } catch (Exception e) {
            log.error("Error approving staff with ID {}: {}", userId, e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> getUserById(int userId) {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            return Optional.ofNullable(entityManager.find(User.class, userId));
        }
    }

    @Override
    public Optional<User> getUserByEmailId(String emailId) {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            TypedQuery<User> query = entityManager.createQuery("FROM User WHERE email = :email", User.class);
            query.setParameter("email", emailId);
            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            log.error("Error fetching user by email {}: {}", emailId, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public boolean isEmailExists(String email) {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class);
            query.setParameter("email", email);
            return query.getSingleResult() > 0;
        }
    }

    @Override
    @Transactional
    public int createUser(User user) {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            entityManager.getTransaction().begin();
            entityManager.persist(user);
            entityManager.flush();
            entityManager.getTransaction().commit();
            return user.getUserID();
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create user");
        }
    }

    @Override
    @Transactional
    public void updateUserToInactive(User user) {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            entityManager.getTransaction().begin();
            user.setActive(false);
            entityManager.merge(user);
            entityManager.getTransaction().commit();
        }
    }

    @Override
    @Transactional
    public void updateUserToActive(User user) {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            entityManager.getTransaction().begin();
            user.setActive(true);
            entityManager.merge(user);
            entityManager.getTransaction().commit();
        }
    }

    @Override
    @Transactional
    public void addAccompaniedGuest(Guest guest) {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            entityManager.getTransaction().begin();
            entityManager.persist(guest);
            entityManager.getTransaction().commit();
        }
    }
}
