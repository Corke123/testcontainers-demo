package com.github.corke123.userservice.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration(proxyBeanMethods = false)
public class LimiterConfiguration {

    @Bean
    public LimiterClient limiterClient(RestClient.Builder builder, LimiterProperties limiterProperties) {
        RestClient client = builder.baseUrl(limiterProperties.url())
                .build();

        RestClientAdapter restClientAdapter = RestClientAdapter.create(client);
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();
        return httpServiceProxyFactory.createClient(LimiterClient.class);
    }
}
