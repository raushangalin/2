package com.example.notification.controller;

import com.example.notification.dto.NotificationRequest;
import com.example.notification.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST контроллер для управления отправкой уведомлений.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final EmailService emailService;

    /**
     * Конструктор с внедрением зависимости.
     *
     * @param emailService сервис отправки email
     */
    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Отправить уведомление через REST API.
     *
     * @param request DTO с данными для отправки
     * @return ResponseEntity с результатом
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request) {
        if (request == null) {
            logger.error("Invalid request: request is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email and operationType are required");
        }

        logger.info("Received notification request: email={}, operationType={}", request.getEmail(), request.getOperationType());

        if (request.getEmail() == null || request.getOperationType() == null) {
            logger.error("Invalid request: email and operationType are required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email and operationType are required");
        }

        try {
            if ("CREATE".equalsIgnoreCase(request.getOperationType())) {
                emailService.sendCreationNotification(request.getEmail());
            } else if ("DELETE".equalsIgnoreCase(request.getOperationType())) {
                emailService.sendDeletionNotification(request.getEmail());
            } else {
                logger.error("Invalid operationType: {}", request.getOperationType());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid operationType. Supported values: CREATE, DELETE");
            }

            return ResponseEntity.ok("Notification sent successfully");
        } catch (IllegalArgumentException ex) {
            logger.error("Validation error: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());
        } catch (Exception ex) {
            logger.error("Error sending notification", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send notification");
        }
    }
}