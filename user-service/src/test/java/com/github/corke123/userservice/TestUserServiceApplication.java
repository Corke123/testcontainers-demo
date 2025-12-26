package com.github.corke123.userservice;

import com.github.corke123.userservice.user.TestcontainersConfig;
import org.springframework.boot.SpringApplication;

public class TestUserServiceApplication {

    static void main(String[] args) {
        SpringApplication.from(UserServiceApplication::main).with(TestcontainersConfig.class).run(args);
    }
}
