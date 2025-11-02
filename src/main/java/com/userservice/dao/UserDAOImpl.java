package com.userservice.dao;

import com.userservice.entity.UserEntity;
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

    public UserDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public UserEntity create(UserEntity userEntity) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            session.persist(userEntity);
            transaction.commit();

            logger.info("User created successfully: {}", userEntity.getId());

            return userEntity;
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
    public Optional<UserEntity> findById(Long id) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            UserEntity userEntity = session.find(UserEntity.class, id);
            transaction.commit();
            logger.debug("Find by id {}: {}", id, userEntity != null ? "found" : "not found");

            return Optional.ofNullable(userEntity);
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
    public Optional<UserEntity> findByEmail(String email) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Query<UserEntity> query = session.createQuery(
                    "FROM UserEntity u WHERE u.email = :email", UserEntity.class);
            query.setParameter("email", email);
            UserEntity userEntity = query.uniqueResult();
            transaction.commit();
            logger.debug("Find by email {}: {}", email, userEntity != null ? "found" : "not found");

            return Optional.ofNullable(userEntity);
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
    public List<UserEntity> findAll() {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Query<UserEntity> query = session.createQuery("FROM UserEntity u ORDER BY u.id", UserEntity.class);
            List<UserEntity> userEntities = query.list();
            logger.debug("Found {} users", userEntities.size());
            transaction.commit();

            return userEntities;
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
    public UserEntity update(UserEntity userEntity) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            UserEntity mergedUserEntity = session.merge(userEntity);
            transaction.commit();
            logger.info("User updated successfully: {}", userEntity.getId());

            return mergedUserEntity;
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

            UserEntity userEntity = session.find(UserEntity.class, id);
            if (userEntity != null) {
                session.remove(userEntity);
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
                    "SELECT COUNT(u) FROM UserEntity u WHERE u.email = :email", Long.class);
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
