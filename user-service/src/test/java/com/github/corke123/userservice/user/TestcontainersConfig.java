package com.github.corke123.userservice.user;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfig {

    @Bean
    @ServiceConnection
    KafkaContainer kafkaContainer() {
        return new KafkaContainer(DockerImageName.parse("apache/kafka:4.1.1"));
    }

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:18.1-alpine3.23");
    }

    @Bean
    GenericContainer<?> wiremockContainer() {
        return new GenericContainer<>("wiremock/wiremock:3.13.2-1-alpine")
                .withExposedPorts(8080)
                .withCopyFileToContainer(
                        MountableFile.forClasspathResource("wiremock"), "/home/wiremock"
                );
    }

    @Bean
    DynamicPropertyRegistrar wiremockProperties(GenericContainer<?> wiremockContainer) {
        return registry -> registry.add("user-service.limiter-service.url",
                () -> "http://%s:%d".formatted(wiremockContainer.getHost(), wiremockContainer.getMappedPort(8080)));
    }

    @Bean
    WireMock wireMock(GenericContainer<?> wiremockContainer) {
        WireMock wireMock = new WireMock(wiremockContainer.getHost(), wiremockContainer.getMappedPort(8080));
        wireMock.register(post(urlMatching("/limits/.*")).willReturn(ok()));
        return wireMock;
    }

    @Bean
    RestTestClient restTestClient(WebApplicationContext context) {
        return RestTestClient.bindToApplicationContext(context).build();
    }
}
