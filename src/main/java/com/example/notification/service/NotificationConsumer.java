package com.example.notification.service;

import com.example.user.dto.UserEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka потребитель для обработки событий пользователей.
 */
@Service
public class NotificationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(NotificationConsumer.class);

    private final EmailService emailService;

    /**
     * Конструктор с внедрением зависимости.
     *
     * @param emailService сервис отправки email
     */
    public NotificationConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Обработать событие из Kafka топика user-events.
     *
     * @param event событие пользователя
     */
    @KafkaListener(topics = "user-events", groupId = "notification-service")
    public void consume(UserEventDto  event) {
        logger.info("Received event from Kafka: operation={}, email={}", event.getOperation(), event.getEmail());

        if (event == null || event.getOperation() == null || event.getEmail() == null) {
            logger.error("Invalid event received: {}", event);
            return;
        }

        try {
            if ("CREATE".equalsIgnoreCase(event.getOperation())) {
                emailService.sendCreationNotification(event.getEmail());
            } else if ("DELETE".equalsIgnoreCase(event.getOperation())) {
                emailService.sendDeletionNotification(event.getEmail());
            } else {
                logger.warn("Unknown operation type: {}", event.getOperation());
            }
        } catch (Exception ex) {
            logger.error("Error processing event: {}", event, ex);
        }
    }
}