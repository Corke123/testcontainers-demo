package com.github.corke123.userservice.user;

import com.github.corke123.userservice.user.UserController.UserRequest;
import com.github.corke123.userservice.user.UserController.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
class UserServiceApplicationTests {

    private RestTestClient restTestClient;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp(WebApplicationContext context) {
        restTestClient = RestTestClient.bindToApplicationContext(context).build();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Given valid user request WHEN create user THEN return 201 created")
    void shouldCreateUser() {
        UserRequest userRequest = new UserRequest("John", "Doe", "john.doe@mail.com");

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
    }

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

    @Test
    @DisplayName("Given user with duplicate email WHEN create user THEN return 409 Conflict")
    void shouldReturn409WhenEmailExists() {
        User existingUser = new User(null, "First", "User", "duplicate@mail.com");
        userRepository.save(existingUser);

        UserRequest duplicateRequest = new UserRequest("Second", "User", "duplicate@mail.com");

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
