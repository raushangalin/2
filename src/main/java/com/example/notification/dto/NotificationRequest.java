package com.example.notification.dto;

/**
 * DTO для REST API запроса отправки уведомления.
 */
public class NotificationRequest {

    private String email;
    private String operationType;

    /**
     * Конструктор по умолчанию.
     */
    public NotificationRequest() {
    }

    /**
     * Конструктор с параметрами.
     *
     * @param email адрес электронной почты
     * @param operationType тип операции (CREATE или DELETE)
     */
    public NotificationRequest(String email, String operationType) {
        this.email = email;
        this.operationType = operationType;
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
     * Получить тип операции.
     *
     * @return тип операции
     */
    public String getOperationType() {
        return operationType;
    }

    /**
     * Установить тип операции.
     *
     * @param operationType тип операции
     */
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
}