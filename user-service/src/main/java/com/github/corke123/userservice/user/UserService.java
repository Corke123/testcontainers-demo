package com.github.corke123.userservice.user;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
class UserService {

    private final UserRepository userRepository;

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    User createUser(User user) {
        return userRepository.save(user);
    }

    List<User> getUsers() {
        return userRepository.findAll();
    }

}
