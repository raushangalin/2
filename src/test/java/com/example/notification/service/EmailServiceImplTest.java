package com.example.notification.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doNothing;

/**
 * Интеграционные тесты для EmailServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailServiceImpl(mailSender);
    }

    @Test
    void testSendCreationNotificationSuccess() {
        String email = "test@example.com";
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendCreationNotification(email);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendDeletionNotificationSuccess() {
        String email = "test@example.com";
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendDeletionNotification(email);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendCreationNotificationWithNullEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            emailService.sendCreationNotification(null);
        });

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendDeletionNotificationWithNullEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            emailService.sendDeletionNotification(null);
        });

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendCreationNotificationWithEmptyEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            emailService.sendCreationNotification("");
        });

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendDeletionNotificationWithEmptyEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            emailService.sendDeletionNotification("");
        });

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendCreationNotificationWithRetrySuccess() {
        String email = "test@example.com";
        doThrow(new MailException("Connection failed") {})
                .doThrow(new MailException("Connection failed") {})
                .doNothing()
                .when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendCreationNotification(email);

        verify(mailSender, times(3)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendDeletionNotificationWithRetryFailure() {
        String email = "test@example.com";
        doThrow(new MailException("Connection failed") {})
                .when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendDeletionNotification(email);

        verify(mailSender, times(3)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendCreationNotificationWithWhitespaceEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            emailService.sendCreationNotification("   ");
        });

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendDeletionNotificationWithValidEmail() {
        String email = "user@test.com";
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendDeletionNotification(email);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}