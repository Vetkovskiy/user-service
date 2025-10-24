package com.userservice.dao;

import com.userservice.entity.User;
import java.util.List;
import java.util.Optional;

/**
 * DAO интерфейс для работы с User
 */
public interface UserDAO {
    
    /**
     * Создать нового пользователя
     * @param user пользователь для создания
     * @return созданный пользователь с присвоенным ID
     */
    User create(User user);
    
    /**
     * Найти пользователя по ID
     * @param id идентификатор пользователя
     * @return Optional с пользователем или пустой Optional
     */
    Optional<User> findById(Long id);
    
    /**
     * Найти пользователя по email
     * @param email email пользователя
     * @return Optional с пользователем или пустой Optional
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Получить всех пользователей
     * @return список всех пользователей
     */
    List<User> findAll();
    
    /**
     * Обновить существующего пользователя
     * @param user пользователь с обновленными данными
     * @return обновленный пользователь
     */
    User update(User user);
    
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
