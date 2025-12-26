package com.github.corke123.testcontainersmailpit;

import org.springframework.boot.autoconfigure.service.connection.ConnectionDetails;

public interface MailpitConnectionDetails extends ConnectionDetails {
    String getHost();

    int getPort();
}
