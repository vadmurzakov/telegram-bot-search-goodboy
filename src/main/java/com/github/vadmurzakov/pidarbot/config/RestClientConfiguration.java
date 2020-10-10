package com.github.vadmurzakov.pidarbot.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfiguration {

    private static final String CONTENT_TYPE = "Content-type";
    private static final String APPLICATION_JSON_VALUE = "application/json";

    @Bean
    public RestTemplate telegramRestTemplate(RestTemplateBuilder builder) {
        return builder.defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE).build();
    }

}
