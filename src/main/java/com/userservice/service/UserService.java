package com.userservice.service;

import com.userservice.entity.User;
import java.util.List;
import java.util.Optional;

/**
 * Service layer для бизнес-логики работы с пользователями
 */
public interface UserService {
    
    /**
     * Создать нового пользователя с валидацией
     */
    User createUser(String name, String email, Integer age);
    
    /**
     * Найти пользователя по ID
     */
    Optional<User> getUserById(Long id);
    
    /**
     * Найти пользователя по email
     */
    Optional<User> getUserByEmail(String email);
    
    /**
     * Получить всех пользователей
     */
    List<User> getAllUsers();
    
    /**
     * Обновить данные пользователя
     */
    User updateUser(Long id, String name, String email, Integer age);
    
    /**
     * Удалить пользователя
     */
    boolean deleteUser(Long id);
}
