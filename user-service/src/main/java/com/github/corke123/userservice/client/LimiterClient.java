package com.github.corke123.userservice.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.PostExchange;

public interface LimiterClient {

    @PostExchange("/limits/{ipAddress}")
    void checkIpLimit(@PathVariable String ipAddress);
}
