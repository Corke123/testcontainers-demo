package com.github.corke123.userservice.user;

import org.springframework.data.repository.ListCrudRepository;

import java.util.UUID;

interface UserRepository extends ListCrudRepository<User, UUID> {
}
