package com.inditex.similarproducts.infrastructure.config;

import com.inditex.similarproducts.application.getsimilarproducts.GetSimilarProductsUseCase;
import com.inditex.similarproducts.domain.ProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {

    @Bean
    public GetSimilarProductsUseCase getSimilarProductsUseCase(ProductRepository productRepository) {
        return new GetSimilarProductsUseCase(productRepository);
    }
}