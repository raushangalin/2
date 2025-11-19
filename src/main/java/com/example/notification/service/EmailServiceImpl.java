package com.example.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Реализация сервиса отправки email уведомлений.
 */
@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    private static final String FROM_EMAIL = "raushangalin@yandex.ru";
    private static final String SUBJECT_CREATION = "Аккаунт успешно создан";
    private static final String SUBJECT_DELETION = "Аккаунт удалён";
    private static final String MESSAGE_CREATION = "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.";
    private static final String MESSAGE_DELETION = "Здравствуйте! Ваш аккаунт был удалён.";
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;

    private final JavaMailSender mailSender;

    /**
     * Конструктор с внедрением зависимости.
     *
     * @param mailSender сервис отправки писем
     */
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendCreationNotification(String email) {
        if (!isValidEmail(email)) {
            logger.error("Invalid email provided: {}", email);
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        sendEmailWithRetry(email, SUBJECT_CREATION, MESSAGE_CREATION);
    }

    @Override
    public void sendDeletionNotification(String email) {
        if (!isValidEmail(email)) {
            logger.error("Invalid email provided: {}", email);
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        sendEmailWithRetry(email, SUBJECT_DELETION, MESSAGE_DELETION);
    }

    /**
     * Отправить письмо с повторными попытками при ошибке.
     *
     * @param email адрес электронной почты
     * @param subject тема письма
     * @param text текст письма
     */
    private void sendEmailWithRetry(String email, String subject, String text) {
        int attempt = 0;
        MailException lastException = null;

        while (attempt < MAX_RETRIES) {
            try {
                sendEmail(email, subject, text);
                logger.info("Email sent successfully to: {}", email);
                return;
            } catch (MailException ex) {
                lastException = ex;
                attempt++;
                logger.warn("Failed to send email to: {}. Attempt: {}/{}", email, attempt, MAX_RETRIES);

                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        logger.error("Retry interrupted for email: {}", email);
                        break;
                    }
                }
            }
        }

        logger.error("Failed to send email to: {} after {} attempts", email, MAX_RETRIES, lastException);
    }

    /**
     * Отправить письмо через JavaMailSender.
     *
     * @param email адрес электронной почты
     * @param subject тема письма
     * @param text текст письма
     * @throws MailException если возникла ошибка при отправке
     */
    private void sendEmail(String email, String subject, String text) throws MailException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    /**
     * Проверить валидность email адреса.
     *
     * @param email адрес электронной почты
     * @return true если адрес валиден, false иначе
     */
    private boolean isValidEmail(String email) {
        return email != null && !email.trim().isEmpty();
    }
}