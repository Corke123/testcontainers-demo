package com.github.corke123.userservice.user;

import com.github.corke123.shared.event.UserCreatedEvent;
import com.github.corke123.userservice.client.LimiterClient;
import com.github.corke123.userservice.publisher.UserCreatedEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class UserService {

    private final UserRepository userRepository;
    private final LimiterClient limiterClient;
    private final UserCreatedEventPublisher userCreatedEventPublisher;

    UserService(UserRepository userRepository, LimiterClient limiterClient, UserCreatedEventPublisher userCreatedEventPublisher) {
        this.userRepository = userRepository;
        this.limiterClient = limiterClient;
        this.userCreatedEventPublisher = userCreatedEventPublisher;
    }

    User createUser(User user, String ipAddress) {
        limiterClient.checkIpLimit(ipAddress);
        User savedUser = userRepository.save(user);
        userCreatedEventPublisher.publishUserCreatedEvent(mapUserToUserCreatedEvent(savedUser));
        return savedUser;
    }

    List<User> getUsers() {
        return userRepository.findAll();
    }

    private UserCreatedEvent mapUserToUserCreatedEvent(User user) {
        return new UserCreatedEvent(user.id(), user.firstName(), user.lastName(), user.email());
    }

}
