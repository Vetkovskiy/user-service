package com.userservice.dao;

import com.userservice.entity.UserEntity;

import java.util.List;
import java.util.Optional;

/**
 * DAO интерфейс для работы с User
 */
public interface UserDAO {
    
    /**
     * Создать нового пользователя
     * @param userEntity пользователь для создания
     * @return созданный пользователь с присвоенным ID
     */
    UserEntity create(UserEntity userEntity);
    
    /**
     * Найти пользователя по ID
     * @param id идентификатор пользователя
     * @return Optional с пользователем или пустой Optional
     */
    Optional<UserEntity> findById(Long id);
    
    /**
     * Найти пользователя по email
     * @param email email пользователя
     * @return Optional с пользователем или пустой Optional
     */
    Optional<UserEntity> findByEmail(String email);
    
    /**
     * Получить всех пользователей
     * @return список всех пользователей
     */
    List<UserEntity> findAll();
    
    /**
     * Обновить существующего пользователя
     * @param userEntity пользователь с обновленными данными
     * @return обновленный пользователь
     */
    UserEntity update(UserEntity userEntity);
    
    /**
     * Удалить пользователя по ID
     * @param id идентификатор пользователя
     * @return true если удален, false если не найден
     */
    boolean delete(Long id);
    
    /**
     * Проверить существование пользователя по email
     * @param email email пользователя
     * @return true если существует
     */
    boolean existsByEmail(String email);
}
