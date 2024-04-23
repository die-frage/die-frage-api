package com.diefrage.businessserver.configs;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {
    @Bean
    public TestRestTemplate restTemplate() {
        return new TestRestTemplate();
    }
}
