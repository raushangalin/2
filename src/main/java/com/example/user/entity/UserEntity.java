package com.example.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Сущность UserEntity для хранения данных пользователя в БД.
 */
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Конструктор по умолчанию.
     */
    public UserEntity() {
    }

    /**
     * Конструктор с параметрами.
     *
     * @param username имя пользователя
     * @param email адрес электронной почты
     * @param password пароль
     */
    public UserEntity(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    /**
     * Получить ID пользователя.
     *
     * @return ID пользователя
     */
    public Long getId() {
        return id;
    }

    /**
     * Установить ID пользователя.
     *
     * @param id ID пользователя
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Получить имя пользователя.
     *
     * @return имя пользователя
     */
    public String getUsername() {
        return username;
    }

    /**
     * Установить имя пользователя.
     *
     * @param username имя пользователя
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Получить адрес электронной почты.
     *
     * @return адрес электронной почты
     */
    public String getEmail() {
        return email;
    }

    /**
     * Установить адрес электронной почты.
     *
     * @param email адрес электронной почты
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Получить пароль.
     *
     * @return пароль
     */
    public String getPassword() {
        return password;
    }

    /**
     * Установить пароль.
     *
     * @param password пароль
     */
    public void setPassword(String password) {
        this.password = password;
    }
}