package com.inditex.similarproducts.application.getsimilarproducts;

import com.inditex.similarproducts.domain.exceptions.SimilarProductsNotFoundException;
import com.inditex.similarproducts.domain.models.Product;
import com.inditex.similarproducts.domain.client.SimilarProductsClient;
import com.inditex.similarproducts.domain.exceptions.SimilarProductsFetchingException;
import com.inditex.similarproducts.domain.usecases.GetSimilarProductsUseCase;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetSimilarProductsUseCaseImplTest {

    private final SimilarProductsClient similarProductsClient = mock(SimilarProductsClient.class);
    private final GetSimilarProductsUseCase getSimilarProductsUseCase = new GetSimilarProductsUseCaseImpl(similarProductsClient);

    @Test
    void shouldReturnSimilarProductsWhenFetchIsSuccessful() {
        // GIVEN
        String productId = "123";
        List<String> similarProductIds = Arrays.asList("456", "789");
        Product product1 = new Product("456", "Dress", new BigDecimal("19.99"), true);
        Product product2 = new Product("789", "Blazer", new BigDecimal("29.99"), false);

        when(similarProductsClient.getSimilarProductIds(productId)).thenReturn(similarProductIds);
        when(similarProductsClient.getProductDetails("456")).thenReturn(Optional.of(product1));
        when(similarProductsClient.getProductDetails("789")).thenReturn(Optional.of(product2));

        // WHEN
        List<Product> result = getSimilarProductsUseCase.getSimilarProducts(productId);

        // THEN
        assertEquals(2, result.size());
        assertEquals("Dress", result.get(0).getName());
        assertEquals("Blazer", result.get(1).getName());

        verify(similarProductsClient).getSimilarProductIds(productId);
        verify(similarProductsClient).getProductDetails("456");
        verify(similarProductsClient).getProductDetails("789");
    }

    @Test
    void shouldThrowExceptionWhenFetchingSimilarProductsFails() {
        // GIVEN
        String productId = "123";
        when(similarProductsClient.getSimilarProductIds(productId)).thenThrow(new RuntimeException("Error database"));

        // WHEN & THEN
        SimilarProductsFetchingException exception = assertThrows(SimilarProductsFetchingException.class, () ->
                getSimilarProductsUseCase.getSimilarProducts(productId)
        );

        assertEquals("Failed to fetch similar products for productId: 123", exception.getMessage());
        verify(similarProductsClient, times(1)).getSimilarProductIds(productId);
        verify(similarProductsClient, never()).getProductDetails(anyString());
    }

    @Test
    void shouldReturnNotFoundWhenNoSimilarProductsAreFound() {
        // GIVEN
        String productId = "123";
        when(similarProductsClient.getSimilarProductIds(productId)).thenReturn(Collections.emptyList());

        // WHEN & THEN
        SimilarProductsNotFoundException exception = assertThrows(SimilarProductsNotFoundException.class, () ->
                getSimilarProductsUseCase.getSimilarProducts(productId)
        );

        assertEquals("No similar products found for productId: 123", exception.getMessage());
        verify(similarProductsClient, times(1)).getSimilarProductIds(productId);
        verify(similarProductsClient, never()).getProductDetails(anyString());
    }

}