package org.example.dao;

import lombok.extern.log4j.Log4j2;
import org.example.entity.Room;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;


import java.util.List;
import java.util.Optional;
@Log4j2
public class RoomDaoImpl implements RoomDao {
    private final SessionFactory sessionFactory;

    public RoomDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void addRoom(Room room) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(room);
            transaction.commit();
            log.info("Room added successfully: {}", room);
        } catch (Exception e) {
            log.error("Error adding room: {}", room, e);
        }
    }

    @Override
    public void updateRoom(Room room) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(room);
            transaction.commit();
            log.info("Room updated successfully: {}", room);
        } catch (Exception e) {
            log.error("Error updating room: {}", room, e);
        }
    }

    @Override
    public List<Room> getAvailableRooms() {
        try (Session session = sessionFactory.openSession()) {
            Query<Room> query = session.createQuery("FROM Room WHERE isAvailable = true", Room.class);
            return query.list();
        } catch (Exception e) {
            log.error("Error fetching available rooms.", e);
        }
        return List.of();
    }

    @Override
    public List<Room> getRoomsUnderMaintenance() {
        try (Session session = sessionFactory.openSession()) {
            Query<Room> query = session.createQuery("FROM Room WHERE isAvailable = false", Room.class);
            return query.list();
        } catch (Exception e) {
            log.error("Error fetching rooms under maintenance.", e);
        }
        return List.of();
    }

    @Override
    public void markRoomUnderMaintenance(int roomId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Room room = session.get(Room.class, roomId);
            if (room != null) {
                room.setAvailable(false);
                session.update(room);
                transaction.commit();
                log.info("Room with ID {} marked as under maintenance.", roomId);
            }
        } catch (Exception e) {
            log.error("Error marking room with ID {} as under maintenance.", roomId, e);
        }
    }

    @Override
    public void markRoomAvailable(int roomId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Room room = session.get(Room.class, roomId);
            if (room != null) {
                room.setAvailable(true);
                session.update(room);
                transaction.commit();
                log.info("Room with ID {} marked as available.", roomId);
            }
        } catch (Exception e) {
            log.error("Error marking room with ID {} as available.", roomId, e);
        }
    }

    @Override
    public Optional<Room> getRoomById(int roomId) {
        try (Session session = sessionFactory.openSession()) {
            Room room = session.get(Room.class, roomId);
            return Optional.ofNullable(room);
        } catch (Exception e) {
            log.error("Error fetching room with ID {}", roomId, e);
        }
        return Optional.empty();
    }
}