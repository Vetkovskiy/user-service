package com.userservice.dao;

import com.userservice.entity.User;
import com.userservice.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Реализация UserDAO с транзакционной логикой
 */
public class UserDAOImpl implements UserDAO {

    private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);
    private final SessionFactory sessionFactory;

    public UserDAOImpl() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    @Override
    public User create(User user) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            session.persist(user);
            transaction.commit();

            logger.info("User created successfully: {}", user.getId());

            return user;

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
                logger.error("Transaction rolled back due to error", e);
            }
            logger.error("Error creating user", e);
            throw e;
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            User user = session.find(User.class, id);
            transaction.commit();
            logger.debug("Find by id {}: {}", id, user != null ? "found" : "not found");

            return Optional.ofNullable(user);

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
                logger.error("Transaction rolled back due to error", e);
            }
            logger.error("Error finding user by id: {}", id, e);
            throw e;
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Query<User> query = session.createQuery(
                    "FROM User u WHERE u.email = :email", User.class);
            query.setParameter("email", email);
            User user = query.uniqueResult();
            transaction.commit();
            logger.debug("Find by email {}: {}", email, user != null ? "found" : "not found");

            return Optional.ofNullable(user);

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
                logger.error("Transaction rolled back due to error", e);
            }
            logger.error("Error finding user by email: {}", email, e);
            throw e;
        }
    }

    @Override
    public List<User> findAll() {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Query<User> query = session.createQuery("FROM User u ORDER BY u.id", User.class);
            List<User> users = query.list();
            logger.debug("Found {} users", users.size());
            transaction.commit();

            return users;

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
                logger.error("Transaction rolled back due to error", e);
            }
            logger.error("Error finding all users", e);

            return Collections.emptyList();
        }
    }

    @Override
    public User update(User user) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            User mergedUser = session.merge(user);
            transaction.commit();
            logger.info("User updated successfully: {}", user.getId());

            return mergedUser;

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
                logger.error("Transaction rolled back due to error", e);
            }
            logger.error("Error updating user", e);
            throw e;
        }
    }

    @Override
    public boolean delete(Long id) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            User user = session.find(User.class, id);
            if (user != null) {
                session.remove(user);
                transaction.commit();
                logger.info("User deleted successfully: {}", id);

                return true;

            } else {
                transaction.rollback();
                logger.warn("User not found for deletion: {}", id);

                return false;
            }
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
                logger.error("Transaction rolled back due to error", e);
            }
            logger.error("Error deleting user", e);
            throw e;
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class);
            query.setParameter("email", email);

            Long count = query.uniqueResult();
            boolean exists = count != null && count > 0;
            transaction.commit();
            logger.debug("Email {} exists: {}", email, exists);

            return exists;

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
                logger.error("Transaction rolled back due to error", e);
            }
            logger.error("Error checking email existence: {}", email, e);

            return false;
        }
    }
}
