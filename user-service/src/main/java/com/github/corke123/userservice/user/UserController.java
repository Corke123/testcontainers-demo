package com.github.corke123.userservice.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok(userService.getUsers().stream()
                .map(user -> new UserResponse(user.id(), user.firstName(), user.lastName(), user.email()))
                .toList());
    }

    @PostMapping
    ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest) {
        var user = new User(null, userRequest.firstName(), userRequest.lastName(), userRequest.email());
        var savedUser = userService.createUser(user);
        return ResponseEntity.created(buildLocation(savedUser.id())).build();
    }

    private URI buildLocation(UUID id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }

    record UserRequest(String firstName, String lastName, String email) {
    }

    record UserResponse(UUID id, String firstName, String lastName, String email) {
    }
}
