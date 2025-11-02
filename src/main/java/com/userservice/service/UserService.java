package com.userservice.service;

import com.userservice.entity.UserEntity;

import java.util.List;
import java.util.Optional;

/**
 * Service layer для бизнес-логики работы с пользователями
 */
public interface UserService {
    
    /**
     * Создать нового пользователя с валидацией
     */
    UserEntity createUser(String name, String email, Integer age);
    
    /**
     * Найти пользователя по ID
     */
    Optional<UserEntity> getUserById(Long id);
    
    /**
     * Найти пользователя по email
     */
    Optional<UserEntity> getUserByEmail(String email);
    
    /**
     * Получить всех пользователей
     */
    List<UserEntity> getAllUsers();
    
    /**
     * Обновить данные пользователя
     */
    UserEntity updateUser(Long id, String name, String email, Integer age);
    
    /**
     * Удалить пользователя
     */
    boolean deleteUser(Long id);
}
