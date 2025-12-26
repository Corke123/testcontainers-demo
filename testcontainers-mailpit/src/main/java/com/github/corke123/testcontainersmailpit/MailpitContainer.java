package com.github.corke123.testcontainersmailpit;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class MailpitContainer extends GenericContainer<MailpitContainer> {

    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("axllent/mailpit:v1.28.0");

    public MailpitContainer() {
        this(DEFAULT_IMAGE_NAME);
    }

    public MailpitContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
        withExposedPorts(1025, 8025);
    }

    public int getSmtpPort() {
        return getMappedPort(1025);
    }

    public int getHttpPort() {
        return getMappedPort(8025);
    }
}
