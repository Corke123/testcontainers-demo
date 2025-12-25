package com.github.corke123.userservice.user;

import com.github.corke123.userservice.client.LimiterClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class UserService {

    private final UserRepository userRepository;
    private final LimiterClient limiterClient;

    UserService(UserRepository userRepository, LimiterClient limiterClient) {
        this.userRepository = userRepository;
        this.limiterClient = limiterClient;
    }

    User createUser(User user, String ipAddress) {
        limiterClient.checkIpLimit(ipAddress);
        return userRepository.save(user);
    }

    List<User> getUsers() {
        return userRepository.findAll();
    }

}
