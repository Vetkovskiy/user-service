package com.userservice.service;

import com.userservice.dao.UserDAO;
import com.userservice.entity.User;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Реализация сервисного слоя с бизнес-логикой и валидацией
 */
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    private final UserDAO userDAO;


    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public User createUser(String name, String email, Integer age) {
        logger.debug("Creating user: name={}, email={}, age={}", name, email, age);
        
        // Валидация входных данных
        validateUserData(name, email, age);
        
        // Проверка уникальности email
        if (userDAO.existsByEmail(email)) {
            logger.warn("Attempt to create user with existing email: {}", email);
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }
        
        User user = new User(name, email, age);

        try {
            return userDAO.create(user);
        } catch (ConstraintViolationException e) {
            logger.warn("Email already exists");
            throw new IllegalArgumentException("Email already exists");
        }
    }

    @Override
    public Optional<User> getUserById(Long id) {
        if (id == null || id <= 0) {
            logger.warn("Invalid user ID: {}", id);
            throw new IllegalArgumentException("User ID must be positive");
        }
        return userDAO.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            logger.warn("Empty email provided");
            throw new IllegalArgumentException("Email cannot be empty");
        }

        return userDAO.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    @Override
    public User updateUser(Long id, String name, String email, Integer age) {
        logger.debug("Updating user: id={}, name={}, email={}, age={}", id, name, email, age);
        
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        
        // Проверка существования пользователя
        User existingUser = userDAO.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User with ID " + id + " not found"));
        
        // Валидация новых данных
        validateUserData(name, email, age);
        
        // Проверка уникальности email (если изменился)
        if (!existingUser.getEmail().equals(email)) {
            if (userDAO.existsByEmail(email)) {
                logger.warn("Attempt to update user with existing email: {}", email);
                throw new IllegalArgumentException("User with email " + email + " already exists");
            }
        }
        
        // Обновление данных
        existingUser.setName(name);
        existingUser.setEmail(email);
        existingUser.setAge(age);
        
        return userDAO.update(existingUser);
    }

    @Override
    public boolean deleteUser(Long id) {
        if (id == null || id <= 0) {
            logger.warn("Invalid user ID for deletion: {}", id);
            throw new IllegalArgumentException("User ID must be positive");
        }
        
        boolean deleted = userDAO.delete(id);
        if (!deleted) {
            logger.warn("User not found for deletion: {}", id);
        }

        return deleted;
    }

    /**
     * Валидация данных пользователя
     */
    private void validateUserData(String name, String email, Integer age) {
        // Валидация имени
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Name cannot exceed 100 characters");
        }
        
        // Валидация email
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (!email.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (email.length() > 150) {
            throw new IllegalArgumentException("Email cannot exceed 150 characters");
        }
        
        // Валидация возраста
        if (age != null) {
            if (age < 0 || age > 150) {
                throw new IllegalArgumentException("Age must be between 0 and 150");
            }
        }
    }
}
