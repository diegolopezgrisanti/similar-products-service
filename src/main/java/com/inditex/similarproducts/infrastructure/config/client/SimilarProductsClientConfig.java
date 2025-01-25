package com.inditex.similarproducts.infrastructure.config.client;

import com.inditex.similarproducts.infrastructure.client.SimilarProductsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SimilarProductsClientConfig {

    @Value("${clients.similar_products.url}")
    private String similarProductsUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public SimilarProductsClient similarProductsService(
            RestTemplate restTemplate) {
        return new SimilarProductsClient(restTemplate, similarProductsUrl);
    }
}