package com.github.corke123.limiterservice.limiter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "limiter")
public record LimiterProperties(Long windowDurationSeconds, Long maxAttempts) {
}
