package com.github.corke123.shared.event;

import java.util.UUID;

public record UserCreatedEvent(UUID id, String firstName, String lastName, String email) {
}
