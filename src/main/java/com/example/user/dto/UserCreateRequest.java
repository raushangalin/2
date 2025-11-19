package com.example.user.dto;

/**
 * DTO для создания нового пользователя через REST API.
 */
public class UserCreateRequest {

    private String username;
    private String email;
    private String password;

    /**
     * Конструктор по умолчанию.
     */
    public UserCreateRequest() {
    }

    /**
     * Конструктор с параметрами.
     *
     * @param username имя пользователя
     * @param email адрес электронной почты
     * @param password пароль
     */
    public UserCreateRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
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