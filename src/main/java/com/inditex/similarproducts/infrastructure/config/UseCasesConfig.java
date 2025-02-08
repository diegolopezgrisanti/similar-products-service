package com.inditex.similarproducts.infrastructure.config;

import com.inditex.similarproducts.application.getsimilarproducts.GetSimilarProductsUseCaseImpl;
import com.inditex.similarproducts.domain.client.SimilarProductsClient;
import com.inditex.similarproducts.domain.usecases.GetSimilarProductsUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {

    @Bean
    public GetSimilarProductsUseCase getSimilarProductsUseCase(SimilarProductsClient similarProductsClient) {
        return new GetSimilarProductsUseCaseImpl(similarProductsClient);
    }
}