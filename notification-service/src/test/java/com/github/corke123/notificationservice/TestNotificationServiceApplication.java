package com.github.corke123.notificationservice;

import com.github.corke123.notificationservice.notification.TestcontainersConfig;
import org.springframework.boot.SpringApplication;

public class TestNotificationServiceApplication {

    static void main(String[] args) {
        SpringApplication.from(NotificationServiceApplication::main).with(TestcontainersConfig.class).run(args);
    }
}
