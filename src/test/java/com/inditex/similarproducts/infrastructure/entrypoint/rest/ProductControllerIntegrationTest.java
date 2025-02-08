package com.inditex.similarproducts.infrastructure.entrypoint.rest;

import com.inditex.similarproducts.domain.client.SimilarProductsClient;
import com.inditex.similarproducts.domain.models.Product;
import com.inditex.similarproducts.infrastructure.entrypoint.rest.response.ProductResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class ProductControllerIntegrationTest {

    @Autowired
    private ProductController productController;

    @MockBean
    private SimilarProductsClient similarProductsClient;

    @Test
    void shouldReturnSimilarProductsWhenFetchIsSuccessful() {
        // GIVEN
        String productId = "123";
        List<String> similarProductIds = Arrays.asList("456", "789");
        when(similarProductsClient.getSimilarProductIds(productId)).thenReturn(similarProductIds);

        Product product1 = new Product("456", "Dress", new BigDecimal("19.99"), true);
        Product product2 = new Product("789", "Blazer", new BigDecimal("29.99"), false);
        when(similarProductsClient.getProductDetails("456")).thenReturn(Optional.of(product1));
        when(similarProductsClient.getProductDetails("789")).thenReturn(Optional.of(product2));

        ProductResponseDTO expectedProductSimilar1 = new ProductResponseDTO(
                "456", "Dress", new BigDecimal("19.99"), true
        );
        ProductResponseDTO expectedProductSimilar2 = new ProductResponseDTO(
                "789", "Blazer", new BigDecimal("29.99"), false
        );

        // WHEN
        ResponseEntity<List<ProductResponseDTO>> responseEntity = productController.getSimilarProducts(productId);
        List<ProductResponseDTO> actualResponse = responseEntity.getBody();

        // THEN
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actualResponse)
                .isNotNull()
                .hasSize(2)
                .containsExactlyInAnyOrder(expectedProductSimilar1, expectedProductSimilar2);
    }

}