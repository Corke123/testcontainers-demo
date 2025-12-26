package com.github.corke123.notificationservice.notification;

import com.github.corke123.shared.event.UserCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    private final NotificationService notificationService;

    NotificationConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(
            topics = "${notification-service.kafka.topics.user-created}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleUserCreated(UserCreatedEvent event) {
        notificationService.sendWelcomeEmail(event);
    }
}
