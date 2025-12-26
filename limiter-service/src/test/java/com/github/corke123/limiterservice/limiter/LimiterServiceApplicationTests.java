package com.github.corke123.limiterservice.limiter;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.time.Duration;

import static com.github.corke123.limiterservice.limiter.TestcontainersConfig.REDIS_CONTAINER;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfig.class)
class LimiterServiceApplicationTests {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private RestTestClient restTestClient;

    @BeforeAll
    static void startRedis() {
        REDIS_CONTAINER.start();
    }

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
    }

    @BeforeEach
    void setUp(WebApplicationContext context) {
        restTestClient = RestTestClient.bindToApplicationContext(context).build();
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb();
    }

    @Test
    @DisplayName("GIVEN fresh IP WHEN checking limit THEN return 200 OK")
    void shouldAllowRequestWhenUnderLimit() {
        String ip = "192.168.0.1";

        restTestClient.post()
                .uri("/limits/{ip}", ip)
                .exchange()
                .expectStatus().isOk();

        String count = redisTemplate.opsForValue().get("rate_limit:" + ip);
        assertThat(count).isEqualTo("1");
    }

    @Test
    @DisplayName("GIVEN IP over limit WHEN checking limit THEN return 429 Too Many Requests")
    void shouldBlockRequestWhenLimitExceeded() {
        String ip = "10.0.0.99";
        String key = "rate_limit:" + ip;

        redisTemplate.opsForValue().set(key, "5");
        redisTemplate.expire(key, Duration.ofSeconds(60));

        restTestClient.post()
                .uri("/limits/{ip}", ip)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS);

        assertThat(redisTemplate.opsForValue().get(key)).isEqualTo("6");
    }

}
