package com.example.notification.service;

/**
 * Интерфейс для сервиса отправки email уведомлений.
 */
public interface EmailService {

    /**
     * Отправить письмо при создании пользователя.
     *
     * @param email адрес электронной почты
     */
    void sendCreationNotification(String email);

    /**
     * Отправить письмо при удалении пользователя.
     *
     * @param email адрес электронной почты
     */
    void sendDeletionNotification(String email);
}