package com.inditex.similarproducts.infrastructure.entrypoint.rest;

import com.inditex.similarproducts.domain.exceptions.SimilarProductsFetchingException;
import com.inditex.similarproducts.domain.exceptions.SimilarProductsNotFoundException;
import com.inditex.similarproducts.domain.models.Product;
import com.inditex.similarproducts.domain.usecases.GetSimilarProductsUseCase;
import com.inditex.similarproducts.infrastructure.entrypoint.rest.response.ProductResponseDTO;
import com.inditex.similarproducts.infrastructure.mappers.ProductMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetSimilarProductsUseCase getSimilarProductsUseCase;

    @MockBean
    private ProductMapper productMapper;

    @Test
    void shouldReturnSimilarProductsWhenFetchIsSuccessful() throws Exception {
        // GIVEN
        String productId = "123";
        List<Product> products = Arrays.asList(
                new Product("456", "Dress", new BigDecimal("19.99"), true),
                new Product("789", "Blazer", new BigDecimal("29.99"), false)
        );
        when(getSimilarProductsUseCase.getSimilarProducts(productId)).thenReturn(products);

        when(productMapper.toResponseDTO(products.get(0)))
                .thenReturn(new ProductResponseDTO("456", "Dress", new BigDecimal("19.99"), true));
        when(productMapper.toResponseDTO(products.get(1)))
                .thenReturn(new ProductResponseDTO("789", "Blazer", new BigDecimal("29.99"), false));

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
        verify(productMapper, times(1)).toResponseDTO(products.get(0));
        verify(productMapper, times(1)).toResponseDTO(products.get(1));
    }

    @Test
    void shouldReturnNotFoundWhenNoSimilarProducts() throws Exception {
        // GIVEN
        String productId = "123";
        when(getSimilarProductsUseCase
                .getSimilarProducts(productId))
                .thenThrow(
                        new SimilarProductsNotFoundException("No similar products found for productId: " + productId));

        // WHEN & THEN
        mockMvc.perform(get("/product/{productId}/similar", productId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("No similar products found for productId: 123"));

        verify(getSimilarProductsUseCase, times(1)).getSimilarProducts(productId);
    }

    @Test
    void shouldReturnInternalServerErrorWhenUnexpectedErrorOccurs() throws Exception {
        // GIVEN
        String productId = "123";
        when(getSimilarProductsUseCase
                .getSimilarProducts(productId))
                .thenThrow(
                        new SimilarProductsFetchingException("Failed to fetch similar products for productId: " + productId));

        // WHEN & THEN
        mockMvc.perform(get("/product/{productId}/similar", productId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                        .value("Failed to fetch similar products for productId: 123"));

        verify(getSimilarProductsUseCase, times(1)).getSimilarProducts(productId);
    }

    @Test
    void shouldReturnInternalServerErrorForRuntimeException() throws Exception {
        // GIVEN
        String productId = "123";
        when(getSimilarProductsUseCase
                .getSimilarProducts(productId))
                .thenThrow(
                        new RuntimeException("Unexpected error occurred"));

        // WHEN & THEN
        mockMvc.perform(get("/product/{productId}/similar", productId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                        .value("Internal server error, please try later"));

        verify(getSimilarProductsUseCase, times(1)).getSimilarProducts(productId);
    }

    @Test
    void shouldReturnBadRequestForHttpClientErrorException() throws Exception {
        // GIVEN
        String productId = "123";
        when(getSimilarProductsUseCase
                .getSimilarProducts(productId))
                .thenThrow(
                        new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        // WHEN & THEN
        mockMvc.perform(get("/product/{productId}/similar", productId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("400 Bad Request"));

        verify(getSimilarProductsUseCase, times(1)).getSimilarProducts(productId);
    }

}