package com.github.corke123.userservice.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("user")
record User(@Id UUID id, String firstName, String lastName, String email) {
}
