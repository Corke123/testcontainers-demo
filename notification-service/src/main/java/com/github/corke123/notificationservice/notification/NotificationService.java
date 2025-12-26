package com.github.corke123.notificationservice.notification;

import com.github.corke123.shared.event.UserCreatedEvent;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final JavaMailSender mailSender;

    NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendWelcomeEmail(UserCreatedEvent event) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(event.email());
            message.setSubject("Welcome to Our Platform!");
            message.setText(formatWelcomeMessage(event.firstName(), event.lastName()));

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", event.email(), e);
        }
    }

    private static @NonNull String formatWelcomeMessage(String firstName, String lastName) {
        return """
                Hello %s %s,
                
                Welcome to our platform! We're excited to have you on board.
                
                Best regards,
                The Team""".formatted(firstName, lastName);
    }
}
