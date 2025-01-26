package com.inditex.similarproducts.infrastructure.config.client;

import com.inditex.similarproducts.infrastructure.client.SimilarProductsClient;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;

@Configuration
public class SimilarProductsClientConfig {

    @Value("${clients.similar_products.url}")
    private String similarProductsUrl;

    @Value("${clients.similar_products.retry.max_attempts}")
    private int maxAttempts;

    @Value("${clients.similar_products.retry.wait_duration}")
    private long waitDuration;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Retry retry() {
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(maxAttempts)
                .waitDuration(Duration.ofMillis(waitDuration))
                .retryExceptions(IOException.class, HttpServerErrorException.class)
                .ignoreExceptions(IllegalArgumentException.class)
                .build();

        return Retry.of("similarProductsRetry", retryConfig);
    }

    @Bean
    public SimilarProductsClient similarProductsService(
            RestTemplate restTemplate, Retry retry) {
        return new SimilarProductsClient(restTemplate, similarProductsUrl, retry);
    }
}