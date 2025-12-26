package com.github.corke123.testcontainersmailpit;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@AutoConfiguration
public class MailpitServiceConnectionAutoConfiguration {

    @Bean
    @ConditionalOnBean(MailpitConnectionDetails.class)
    public JavaMailSender javaMailSender(MailpitConnectionDetails details) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(details.getHost());
        mailSender.setPort(details.getPort());
        return mailSender;
    }
}
