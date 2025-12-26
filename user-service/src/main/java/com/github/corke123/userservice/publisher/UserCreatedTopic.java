package com.github.corke123.userservice.publisher;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "user-service.kafka.topics.user-created")
public record UserCreatedTopic(String name) {
}
