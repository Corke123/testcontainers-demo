package com.github.corke123.userservice.user;

import com.github.corke123.shared.event.UserCreatedEvent;
import com.github.corke123.userservice.user.UserController.UserRequest;
import com.github.corke123.userservice.user.UserController.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@EnableWireMock(
        @ConfigureWireMock(name = "limiter-service", baseUrlProperties = "user-service.limiter-service.url")
)
@EmbeddedKafka(
        partitions = 1,
        topics = {"${user-service.kafka.topics.user-created.name}"}
)
@Import({KafkaTestSupportConfig.class})
class UserServiceApplicationTests {

    private RestTestClient restTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KafkaTestSupportConfig.UserCreatedTestReceiver receiver;

    @BeforeEach
    void setUp(WebApplicationContext context) {
        restTestClient = RestTestClient.bindToApplicationContext(context).build();
        userRepository.deleteAll();
    }

    @Nested
    class CreateUserTests {

        @Test
        @DisplayName("GIVEN rate limit not exceeded WHEN create user THEN return 201 Created")
        void shouldCreateUser() {
            UserRequest userRequest = new UserRequest("John", "Doe", "john.doe@mail.com");

            stubFor(post(urlMatching("/limits/.*")).willReturn(ok()));

            restTestClient.post()
                    .uri("/users")
                    .body(userRequest)
                    .exchange()
                    .expectStatus().isCreated();

            List<User> persistedUsers = userRepository.findAll();
            assertThat(persistedUsers).hasSize(1);
            final User savedUser = persistedUsers.getFirst();
            assertThat(savedUser).satisfies(user -> {
                assertThat(user.id()).isNotNull();
                assertThat(user.firstName()).isEqualTo(userRequest.firstName());
                assertThat(user.lastName()).isEqualTo(userRequest.lastName());
                assertThat(user.email()).isEqualTo(userRequest.email());
            });

            await().atMost(5, TimeUnit.SECONDS)
                    .untilAsserted(() -> {
                        UserCreatedEvent receivedEvent = receiver.getReceivedEvent();
                        assertThat(receivedEvent).isNotNull();
                        assertThat(receivedEvent).isEqualTo(
                                new UserCreatedEvent(savedUser.id(), savedUser.firstName(), savedUser.lastName(), savedUser.email()));
                    });
        }

        @Test
        @DisplayName("GIVEN rate limit exceeded WHEN create user THEN return 429 Too Many Requests")
        void shouldBlockUserCreationWhenRateLimitExceeded() {
            UserRequest userRequest = new UserRequest("Blocked", "User", "blocked@mail.com");

            stubFor(post(urlMatching("/limits/.*")).willReturn(aResponse().withStatus(429)));

            restTestClient.post()
                    .uri("/users")
                    .body(userRequest)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS)
                    .expectBody()
                    .jsonPath("$.title").isEqualTo("Rate Limit Exceeded");

            assertThat(userRepository.findAll()).isEmpty();
        }

        @Test
        @DisplayName("Given user with duplicate email WHEN create user THEN return 409 Conflict")
        void shouldReturn409WhenEmailExists() {
            User existingUser = new User(null, "First", "User", "duplicate@mail.com");
            UserRequest duplicateRequest = new UserRequest("Second", "User", "duplicate@mail.com");

            userRepository.save(existingUser);
            stubFor(post(urlMatching("/limits/.*")).willReturn(ok()));


            restTestClient.post()
                    .uri("/users")
                    .body(duplicateRequest)
                    .exchange()
                    .expectStatus().isEqualTo(org.springframework.http.HttpStatus.CONFLICT)
                    .expectBody()
                    .jsonPath("$.title").isEqualTo("Duplicate Resource")
                    .jsonPath("$.detail").isEqualTo("User with given email already exists");
        }
    }

    @Nested
    class GetUsersTests {
        @Test
        @DisplayName("Given existing users WHEN get users THEN return list of users")
        void shouldReturnAllUsers() {
            User user = new User(null, "John", "Doe", "john.doe@mail.com");
            User savedUser = userRepository.save(user);

            List<UserResponse> responseBody = restTestClient.get()
                    .uri("/users")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(new ParameterizedTypeReference<List<UserResponse>>() {
                    })
                    .returnResult()
                    .getResponseBody();

            assertThat(responseBody).hasSize(1)
                    .first()
                    .isNotNull()
                    .satisfies(userResponse -> {
                        assertThat(userResponse.id()).isEqualTo(savedUser.id());
                        assertThat(userResponse.firstName()).isEqualTo(savedUser.firstName());
                        assertThat(userResponse.lastName()).isEqualTo(savedUser.lastName());
                        assertThat(userResponse.email()).isEqualTo(savedUser.email());
                    });
        }
    }
}
