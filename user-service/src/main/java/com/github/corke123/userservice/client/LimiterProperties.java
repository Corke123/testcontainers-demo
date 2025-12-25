package com.github.corke123.userservice.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "user-service.limiter-service")
public record LimiterProperties(String url) {
}
