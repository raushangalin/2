package com.example.user.service;

import com.example.user.dto.UserEventDto;
import com.example.user.entity.UserEntity;

/**
 * Интерфейс сервиса для управления пользователями.
 */
public interface UserService {

    /**
     * Создать нового пользователя.
     *
     * @param userEntity объект пользователя
     * @return созданный пользователь
     */
    UserEntity createUser(UserEntity userEntity);

    /**
     * Получить пользователя по ID.
     *
     * @param id ID пользователя
     * @return пользователь
     */
    UserEntity getUserById(Long id);

    /**
     * Получить пользователя по email.
     *
     * @param email адрес электронной почты
     * @return пользователь
     */
    UserEntity getUserByEmail(String email);

    /**
     * Обновить пользователя.
     *
     * @param userEntity объект пользователя
     * @return обновленный пользователь
     */
    UserEntity updateUser(UserEntity userEntity);

    /**
     * Удалить пользователя по ID.
     *
     * @param id ID пользователя
     */
    void deleteUser(Long id);

    /**
     * Проверить существование пользователя по email.
     *
     * @param email адрес электронной почты
     * @return true если пользователь существует
     */
    boolean userExists(String email);
}
