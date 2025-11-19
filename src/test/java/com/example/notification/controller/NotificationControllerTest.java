package com.example.notification.controller;

import com.example.notification.dto.NotificationRequest;
import com.example.notification.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Интеграционные тесты для NotificationController.
 */
@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private EmailService emailService;

    private NotificationController notificationController;

    @BeforeEach
    void setUp() {
        notificationController = new NotificationController(emailService);
    }

    @Test
    void testSendNotificationWithCreateOperationSuccess() {
        NotificationRequest request = new NotificationRequest("test@example.com", "CREATE");
        doNothing().when(emailService).sendCreationNotification("test@example.com");

        ResponseEntity<String> response = notificationController.sendNotification(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Notification sent successfully", response.getBody());
        verify(emailService, times(1)).sendCreationNotification("test@example.com");
    }

    @Test
    void testSendNotificationWithDeleteOperationSuccess() {
        NotificationRequest request = new NotificationRequest("test@example.com", "DELETE");
        doNothing().when(emailService).sendDeletionNotification("test@example.com");

        ResponseEntity<String> response = notificationController.sendNotification(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Notification sent successfully", response.getBody());
        verify(emailService, times(1)).sendDeletionNotification("test@example.com");
    }

    @Test
    void testSendNotificationWithNullRequest() {
        ResponseEntity<String> response = notificationController.sendNotification(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testSendNotificationWithNullEmail() {
        NotificationRequest request = new NotificationRequest(null, "CREATE");

        ResponseEntity<String> response = notificationController.sendNotification(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testSendNotificationWithNullOperationType() {
        NotificationRequest request = new NotificationRequest("test@example.com", null);

        ResponseEntity<String> response = notificationController.sendNotification(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testSendNotificationWithInvalidOperationType() {
        NotificationRequest request = new NotificationRequest("test@example.com", "INVALID");

        ResponseEntity<String> response = notificationController.sendNotification(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid operationType. Supported values: CREATE, DELETE", response.getBody());
    }

    @Test
    void testSendNotificationWithEmailServiceException() {
        NotificationRequest request = new NotificationRequest("test@example.com", "CREATE");
        doThrow(new IllegalArgumentException("Invalid email")).when(emailService).sendCreationNotification(any());

        ResponseEntity<String> response = notificationController.sendNotification(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testSendNotificationWithUnexpectedException() {
        NotificationRequest request = new NotificationRequest("test@example.com", "CREATE");
        doThrow(new RuntimeException("Unexpected error")).when(emailService).sendCreationNotification(any());

        ResponseEntity<String> response = notificationController.sendNotification(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to send notification", response.getBody());
    }

    @Test
    void testSendNotificationWithCreateOperationLowercase() {
        NotificationRequest request = new NotificationRequest("test@example.com", "create");
        doNothing().when(emailService).sendCreationNotification("test@example.com");

        ResponseEntity<String> response = notificationController.sendNotification(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(emailService, times(1)).sendCreationNotification("test@example.com");
    }

    @Test
    void testSendNotificationWithDeleteOperationLowercase() {
        NotificationRequest request = new NotificationRequest("test@example.com", "delete");
        doNothing().when(emailService).sendDeletionNotification("test@example.com");

        ResponseEntity<String> response = notificationController.sendNotification(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(emailService, times(1)).sendDeletionNotification("test@example.com");
    }
}