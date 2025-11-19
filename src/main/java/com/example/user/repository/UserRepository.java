package com.example.user.repository;

import com.example.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository для работы с пользователями.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Найти пользователя по email.
     *
     * @param email адрес электронной почты
     * @return пользователь или пусто
     */
    UserEntity findByEmail(String email);

    /**
     * Проверить существование пользователя по email.
     *
     * @param email адрес электронной почты
     * @return true если пользователь существует
     */
    boolean existsByEmail(String email);

    /**
     * Найти пользователя по имени пользователя.
     *
     * @param username имя пользователя
     * @return пользователь или пусто
     */
    UserEntity findByUsername(String username);
}