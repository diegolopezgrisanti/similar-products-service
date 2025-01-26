package com.inditex.similarproducts.infrastructure.config;

import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public Info apiInfo() {
        return new Info()
                .title("Similar Products Service API")
                .version("1.0")
                .description("API REST for retrieving similar products");
    }
}