package com.github.corke123.userservice.user;

import com.github.corke123.shared.event.UserCreatedEvent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.converter.JacksonJsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;

@TestConfiguration
public class KafkaTestSupportConfig {

    @Bean
    public UserCreatedTestReceiver userCreatedTestReceiver() {
        return new UserCreatedTestReceiver();
    }

    @Bean
    public RecordMessageConverter converter() {
        return new JacksonJsonMessageConverter();
    }

    public static class UserCreatedTestReceiver {
        private UserCreatedEvent receivedEvent;

        @KafkaListener(topics = "${user-service.kafka.topics.user-created.name}", groupId = "test-group")
        public void receive(UserCreatedEvent event) {
            receivedEvent = event;
        }

        public UserCreatedEvent getReceivedEvent() {
            return receivedEvent;
        }
    }

}
