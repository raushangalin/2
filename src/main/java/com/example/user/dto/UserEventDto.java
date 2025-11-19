package com.example.user.dto;

/**
 * DTO для события пользователя, отправляемого в Kafka.
 */
public class UserEventDto {

    private String operation;
    private String email;

    /**
     * Конструктор по умолчанию.
     */
    public UserEventDto() {
    }

    /**
     * Конструктор с параметрами.
     *
     * @param operation тип операции (CREATE или DELETE)
     * @param email адрес электронной почты пользователя
     */
    public UserEventDto(String operation, String email) {
        this.operation = operation;
        this.email = email;
    }

    /**
     * Получить тип операции.
     *
     * @return тип операции
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Установить тип операции.
     *
     * @param operation тип операции
     */
    public void setOperation(String operation) {
        this.operation = operation;
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
}