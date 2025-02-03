package com.inditex.similarproducts.infrastructure.entrypoint.rest;

import com.inditex.similarproducts.domain.models.Product;
import com.inditex.similarproducts.domain.usecases.GetSimilarProductsUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest
class ProductControllerContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetSimilarProductsUseCase getSimilarProductsUseCase;

    @Test
    void shouldReturnSimilarProductsWhenFetchIsSuccessful() throws Exception {
        // GIVEN
        String productId = "123";
        List<Product> products = Arrays.asList(
                new Product("456", "Dress", new BigDecimal("19.99"), true),
                new Product("789", "Blazer", new BigDecimal("29.99"), false)
        );
        when(getSimilarProductsUseCase.getSimilarProducts(productId)).thenReturn(products);

        // WHEN & THEN
        mockMvc.perform(get("/product/{productId}/similar", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value("456"))
                .andExpect(jsonPath("$[0].name").value("Dress"))
                .andExpect(jsonPath("$[0].price").value(19.99))
                .andExpect(jsonPath("$[0].availability").value(true))
                .andExpect(jsonPath("$[1].id").value("789"))
                .andExpect(jsonPath("$[1].name").value("Blazer"))
                .andExpect(jsonPath("$[1].price").value(29.99))
                .andExpect(jsonPath("$[1].availability").value(false));

        verify(getSimilarProductsUseCase).getSimilarProducts(productId);
    }

    @Test
    void shouldReturnNotFoundWhenNoSimilarProductsAreFound() throws Exception {
        // GIVEN
        String productId = "123";
        when(getSimilarProductsUseCase.getSimilarProducts(productId)).thenReturn(Collections.emptyList());

        // WHEN & THEN
        mockMvc.perform(get("/product/{productId}/similar", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("No similar products found for productId: " + productId));

        verify(getSimilarProductsUseCase).getSimilarProducts(productId);
    }
}