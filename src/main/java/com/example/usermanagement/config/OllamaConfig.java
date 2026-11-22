package com.example.usermanagement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class OllamaConfig {
    @Bean
    RestClient ollamaRestClient(@Value("${ollama.base-url}") String baseUrl,
                                @Value("${ollama.connect-timeout-seconds:5}") long connectSeconds,
                                @Value("${ollama.read-timeout-seconds:180}") long readSeconds) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(connectSeconds));
        factory.setReadTimeout(Duration.ofSeconds(readSeconds));
        return RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }
}
