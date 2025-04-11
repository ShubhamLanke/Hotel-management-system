package org.example.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.example.entity.Room;
import org.example.persistence.PersistenceManager;

import java.util.List;
import java.util.Optional;

@Log4j2
public class RoomDaoImpl implements RoomDao {

    @Override
    @Transactional
    public void addRoom(Room room) {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            entityManager.getTransaction().begin();
            entityManager.persist(room);
            entityManager.getTransaction().commit();
            log.info("Room added successfully: {}", room);
        } catch (Exception e) {
            log.error("Error adding room: {}", room, e);
        }
    }

    @Override
    @Transactional
    public void updateRoom(Room room) {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            entityManager.getTransaction().begin();
            entityManager.merge(room);
            entityManager.getTransaction().commit();
            log.info("Room updated successfully: {}", room);
        } catch (Exception e) {
            log.error("Error updating room: {}", room, e);
        }
    }

    @Override
    public List<Room> getAvailableRooms() {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            TypedQuery<Room> query = entityManager.createQuery(
                    "SELECT r FROM Room r WHERE r.isAvailable = true ORDER BY r.roomID ASC", Room.class);
            return query.getResultList();
        } catch (Exception e) {
            log.error("Error fetching available rooms.", e);
            return List.of();
        }
    }

    @Override
    public List<Room> getRoomsUnderMaintenance() {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            TypedQuery<Room> query = entityManager.createQuery(
                    "SELECT r FROM Room r WHERE r.isAvailable = false", Room.class);
            return query.getResultList();
        } catch (Exception e) {
            log.error("Error fetching rooms under maintenance.", e);
            return List.of();
        }
    }

    @Override
    @Transactional
    public void markRoomUnderMaintenance(int roomId) {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            entityManager.getTransaction().begin();
            Room room = entityManager.find(Room.class, roomId);
            if (room != null) {
                room.setAvailable(false);
                entityManager.merge(room);
                entityManager.getTransaction().commit();
                log.info("Room with ID {} marked as under maintenance.", roomId);
            }
        } catch (Exception e) {
            log.error("Error marking room with ID {} as under maintenance.", roomId, e);
        }
    }

    @Override
    @Transactional
    public void markRoomAvailable(int roomId) {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            entityManager.getTransaction().begin();
            Room room = entityManager.find(Room.class, roomId);
            if (room != null) {
                room.setAvailable(true);
                entityManager.merge(room);
                entityManager.getTransaction().commit();
                log.info("Room with ID {} marked as available.", roomId);
            }
        } catch (Exception e) {
            log.error("Error marking room with ID {} as available.", roomId, e);
        }
    }

    @Override
    public Optional<Room> getRoomById(int roomId) {
        try (EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager()) {
            Room room = entityManager.find(Room.class, roomId);
            return Optional.ofNullable(room);
        } catch (Exception e) {
            log.error("Error fetching room with ID {}", roomId, e);
            return Optional.empty();
        }
    }
}
