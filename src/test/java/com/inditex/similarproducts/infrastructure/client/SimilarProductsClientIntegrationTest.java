package com.inditex.similarproducts.infrastructure.client;

import com.github.tomakehurst.wiremock.WireMockServer;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.inditex.similarproducts.domain.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SimilarProductsClientIntegrationTest {

    @Autowired
    private RestTemplate restTemplate;

    private SimilarProductsClient similarProductsClient;

    private WireMockServer wireMockServer;

    @BeforeEach
    void setUp() {
        // Starts WireMock server on port 3002
        wireMockServer = new WireMockServer(3002);
        wireMockServer.start();
        configureFor("localhost", 3002);

        // Initializes the client with the base URL pointing to WireMock server
        similarProductsClient = new SimilarProductsClient(restTemplate, "http://localhost:3002");
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void shouldReturnSimilarProductIdsFromMockServer() {
        // GIVEN
        wireMockServer.givenThat(
                WireMock.get(WireMock.urlPathEqualTo("/product/123/similarids"))
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader("Content-Type", "application/json")
                                .withBody("[\"456\", \"789\"]")
                        )
        );
        // WHEN
        List<String> similarProductIds = similarProductsClient.getSimilarProductIds("123");

        // THEN
        assertNotNull(similarProductIds);
        assertEquals(2, similarProductIds.size());
        assertTrue(similarProductIds.contains("456"));
        assertTrue(similarProductIds.contains("789"));
    }

    @Test
    void shouldReturnProductDetailsFromMockServer() {
        // GIVEN
        wireMockServer.givenThat(
                WireMock.get(WireMock.urlPathEqualTo("/product/123"))
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"id\":\"123\", \"name\":\"Product Name\", \"price\":10.99, \"availability\":true}")
                        )
        );
        // WHEN
        Optional<Product> product = similarProductsClient.getProductDetails("123");

        // THEN
        assertTrue(product.isPresent());

        assertEquals("123", product.get().getId());
        assertEquals("Product Name", product.get().getName());
        assertEquals(new BigDecimal("10.99"), product.get().getPrice());
        assertTrue(product.get().isAvailability());
    }

    @Test
    void shouldReturnEmptyListWhenSimilarProductIdsNotFound() {
        // GIVEN
        wireMockServer.givenThat(
                WireMock.get(WireMock.urlPathEqualTo("/product/123/similarids"))
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.NOT_FOUND.value())
                        )
        );

        // WHEN
        List<String> result = similarProductsClient.getSimilarProductIds("123");

        // THEN
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyOptionalWhenProductDetailsNotFound() {
        // GIVEN
        wireMockServer.givenThat(
                WireMock.get(WireMock.urlPathEqualTo("/product/123"))
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.NOT_FOUND.value())
                        )
        );
        // WHEN
        Optional<Product> result = similarProductsClient.getProductDetails("123");

        // THEN
        assertTrue(result.isEmpty());
    }
}