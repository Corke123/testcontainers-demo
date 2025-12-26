package com.github.corke123.notificationservice.notification;

import com.github.corke123.shared.event.UserCreatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

@SpringBootTest(properties = {
        "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
        "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JacksonJsonSerializer"
})
@EmbeddedKafka(
        partitions = 1,
        topics = {"${notification-service.kafka.topics.user-created}"}
)
class NotificationServiceApplicationTests {

    @Value("${notification-service.kafka.topics.user-created}")
    private String topicName;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @MockitoBean
    private JavaMailSender mailSender;

    private final ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

    @Test
    @DisplayName("GIVEN UserCreatedEvent is received, WHEN it is processed, THEN welcome email is sent")
    void shouldProcessNotification() {
        var event = new UserCreatedEvent(UUID.randomUUID(), "John", "Doe", "john.doe@example.com");

        kafkaTemplate.send(topicName, event);

        await().atMost(Duration.ofSeconds(5))
                .pollInterval(Duration.ofMillis(100))
                .untilAsserted(() -> {
                    verify(mailSender).send(messageCaptor.capture());
                    assertThat(messageCaptor.getValue())
                            .extracting(SimpleMailMessage::getTo, SimpleMailMessage::getSubject, SimpleMailMessage::getText)
                            .containsExactly(
                                    new String[]{"john.doe@example.com"},
                                    "Welcome to Our Platform!",
                                    """
                                            Hello John Doe,
                                            
                                            Welcome to our platform! We're excited to have you on board.
                                            
                                            Best regards,
                                            The Team"""
                            );
                });
    }

}
