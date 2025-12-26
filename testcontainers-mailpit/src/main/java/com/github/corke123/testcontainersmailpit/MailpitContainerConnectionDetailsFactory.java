package com.github.corke123.testcontainersmailpit;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.testcontainers.service.connection.ContainerConnectionDetailsFactory;
import org.springframework.boot.testcontainers.service.connection.ContainerConnectionSource;

public class MailpitContainerConnectionDetailsFactory extends ContainerConnectionDetailsFactory<MailpitContainer, MailpitConnectionDetails> {

    @Override
    protected @Nullable MailpitConnectionDetails getContainerConnectionDetails(@NonNull ContainerConnectionSource<MailpitContainer> source) {
        return new MailpitContainerConnectionDetails(source);
    }

    private static final class MailpitContainerConnectionDetails extends ContainerConnectionDetails<MailpitContainer>
            implements MailpitConnectionDetails {

        private MailpitContainerConnectionDetails(ContainerConnectionSource<MailpitContainer> source) {
            super(source);
        }

        @Override
        public String getHost() {
            return getContainer().getHost();
        }

        @Override
        public int getPort() {
            return getContainer().getSmtpPort();
        }
    }
}
