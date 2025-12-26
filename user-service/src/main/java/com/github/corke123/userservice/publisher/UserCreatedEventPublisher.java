package com.github.corke123.userservice.publisher;

import com.github.corke123.shared.event.UserCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserCreatedEventPublisher {

    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;
    private final UserCreatedTopic userCreatedTopic;

    public UserCreatedEventPublisher(KafkaTemplate<String, UserCreatedEvent> kafkaTemplate,
                                     UserCreatedTopic userCreatedTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.userCreatedTopic = userCreatedTopic;
    }

    public void publishUserCreatedEvent(UserCreatedEvent event) {
        kafkaTemplate.send(userCreatedTopic.name(), event.id().toString(), event);
    }
}
