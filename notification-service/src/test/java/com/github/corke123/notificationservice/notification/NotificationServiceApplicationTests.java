package com.github.corke123.notificationservice.notification;

import com.github.corke123.shared.event.UserCreatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(properties = {
        "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
        "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JacksonJsonSerializer"
})
@Import(TestcontainersConfig.class)
class NotificationServiceApplicationTests {

    @Value("${notification-service.kafka.topics.user-created}")
    private String topicName;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private RestClient mailpitClient;

    @Test
    @DisplayName("GIVEN UserCreatedEvent is received, WHEN it is processed, THEN welcome email is sent")
    void shouldProcessNotification() {
        var event = new UserCreatedEvent(UUID.randomUUID(), "John", "Doe", "john.doe@example.com");

        kafkaTemplate.send(topicName, event);

        await().atMost(Duration.ofSeconds(5))
                .pollInterval(Duration.ofMillis(100))
                .untilAsserted(() -> {
                    MailpitResponse mailpitResponse = mailpitClient.get()
                            .uri("/api/v1/messages")
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .body(MailpitResponse.class);

                    System.out.println("mailpitResponse = " + mailpitResponse);

                    assertThat(mailpitResponse).isNotNull();
                    assertThat(mailpitResponse.messages).hasSize(1);
                    MailpitMessage receivedMessage = mailpitResponse.messages.getFirst();

                    assertThat(receivedMessage.To()).hasSize(1);
                    Recipient recipient = receivedMessage.To().getFirst();
                    assertThat(recipient.Address()).isEqualTo("john.doe@example.com");

                    assertThat(receivedMessage.Subject()).isEqualTo("Welcome to Our Platform!");
                    assertThat(receivedMessage.Snippet()).isEqualTo("Hello John Doe, Welcome to our platform! We're excited to have you on board. Best regards, The Team");
                });
    }

    record MailpitResponse(List<MailpitMessage> messages, int total) {
    }

    record MailpitMessage(String ID, String Subject, List<Recipient> To, String Snippet) {
    }

    record Recipient(String Name, String Address) {
    }

}
