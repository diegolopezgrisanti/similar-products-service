package com.inditex.similarproducts.application.getsimilarproducts;

import com.inditex.similarproducts.domain.Product;
import com.inditex.similarproducts.domain.ProductRepository;
import com.inditex.similarproducts.domain.exceptions.SimilarProductsFetchingException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetSimilarProductsUseCaseTest {

    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final GetSimilarProductsUseCase getSimilarProductsUseCase = new GetSimilarProductsUseCase(productRepository);

    @Test
    void shouldReturnSimilarProductsWhenFetchIsSuccessful() {
        // GIVEN
        String productId = "123";
        List<String> similarProductIds = Arrays.asList("456", "789");
        Product product1 = new Product("456", "Dress", new BigDecimal("19.99"), true);
        Product product2 = new Product("789", "Blazer", new BigDecimal("29.99"), false);

        when(productRepository.getSimilarProductIds(productId)).thenReturn(similarProductIds);
        when(productRepository.getProductDetails("456")).thenReturn(product1);
        when(productRepository.getProductDetails("789")).thenReturn(product2);

        // WHEN
        List<Product> result = getSimilarProductsUseCase.getSimilarProducts(productId);

        // THEN
        assertEquals(2, result.size());
        assertEquals("Dress", result.get(0).getName());
        assertEquals("Blazer", result.get(1).getName());

        verify(productRepository).getSimilarProductIds(productId);
        verify(productRepository).getProductDetails("456");
        verify(productRepository).getProductDetails("789");
    }

    @Test
    void shouldReturnEmptyListWhenNoSimilarProductsAreFound() {
        // GIVEN
        String productId = "123";
        when(productRepository.getSimilarProductIds(productId)).thenReturn(Collections.emptyList());

        // WHEN
        List<Product> result = getSimilarProductsUseCase.getSimilarProducts(productId);

        // THEN
        assertTrue(result.isEmpty());

        verify(productRepository).getSimilarProductIds(productId);
        verify(productRepository, never()).getProductDetails(anyString());
    }

    @Test
    void shouldThrowExceptionWhenFetchingSimilarProductsFails() {
        // GIVEN
        String productId = "123";
        when(productRepository.getSimilarProductIds(productId)).thenThrow(new RuntimeException("Error database"));

        // WHEN & THEN
        SimilarProductsFetchingException exception = assertThrows(SimilarProductsFetchingException.class, () ->
                getSimilarProductsUseCase.getSimilarProducts(productId));

        assertEquals("Failed to fetch similar products for productId: 123", exception.getMessage());

        verify(productRepository).getSimilarProductIds(productId);
        verify(productRepository, never()).getProductDetails(anyString());
    }

}