package com.github.corke123.limiterservice;

import com.github.corke123.limiterservice.limiter.TestcontainersConfig;
import org.springframework.boot.SpringApplication;

public class TestLimiterServiceApplication {

    static void main(String[] args) {
        SpringApplication.from(LimiterServiceApplication::main).with(TestcontainersConfig.class).run(args);
    }
}
