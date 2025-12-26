package com.github.corke123.limiterservice.limiter;

import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfig {

    static RedisContainer redisContainer = new RedisContainer(DockerImageName.parse("redis:8.4.0-alpine")).withExposedPorts(6379);

}
