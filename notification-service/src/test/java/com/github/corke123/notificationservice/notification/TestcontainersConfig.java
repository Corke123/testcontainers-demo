package com.github.corke123.notificationservice.notification;

import com.github.corke123.testcontainersmailpit.MailpitContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfig {

    @Bean
    @ServiceConnection
    KafkaContainer kafkaContainer() {
        return new KafkaContainer(DockerImageName.parse("apache/kafka:4.1.1"));
    }

    @Bean
    @ServiceConnection
    MailpitContainer mailpitContainer() {
        return new MailpitContainer(DockerImageName.parse("axllent/mailpit:v1.28.0"));
    }

    @Bean
    RestClient restTestClient(MailpitContainer mailpit) {
        return RestClient.builder()
                .baseUrl("http://%s:%s".formatted(mailpit.getHost(), mailpit.getHttpPort()))
                .build();
    }
}
