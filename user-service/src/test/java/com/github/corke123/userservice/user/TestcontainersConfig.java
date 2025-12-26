package com.github.corke123.userservice.user;

import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfig {

    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("apache/kafka:4.1.1"));

    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:18.1-alpine3.23");

    static GenericContainer<?> wiremockContainer = new GenericContainer<>("wiremock/wiremock:3.13.2-1-alpine")
            .withExposedPorts(8080);
}
