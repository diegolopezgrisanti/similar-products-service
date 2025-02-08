package com.inditex.similarproducts.infrastructure.client;

import com.github.tomakehurst.wiremock.WireMockServer;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import com.inditex.similarproducts.domain.client.SimilarProductsClient;
import com.inditex.similarproducts.domain.models.Product;
import io.github.resilience4j.retry.Retry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SimilarProductsClientImplIntegrationTest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Retry retry;

    private SimilarProductsClient similarProductsClient;
    private WireMockServer wireMockServer;

    @Value("${clients.similar_products.url}")
    private String similarProductsUrl;

    @BeforeEach
    void setUp() {
        // Starts WireMock server on port 3002
        wireMockServer = new WireMockServer(3002);
        wireMockServer.start();
        configureFor("localhost", 3002);

        // Initializes the client with the base URL pointing to WireMock server
        similarProductsClient = new SimilarProductsClientImpl(restTemplate, similarProductsUrl, retry);
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
                                .withBody("{\"id\":\"123\", \"name\":\"Product 123\", \"price\":10.99, \"availability\":true}")
                        )
        );
        // WHEN
        Optional<Product> product = similarProductsClient.getProductDetails("123");

        // THEN
        assertTrue(product.isPresent());

        assertEquals("123", product.get().getId());
        assertEquals("Product 123", product.get().getName());
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

    @Test
    void shouldReturnEmptyListAfterThreeFailedAttempts() {
        // GIVEN
        wireMockServer.stubFor(
                WireMock.get(WireMock.urlPathEqualTo("/product/123/similarids"))
                        .inScenario("Retry Scenario")
                        .whenScenarioStateIs(Scenario.STARTED)
                        .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        )
                        .willSetStateTo("Second Attempt")
        );
        wireMockServer.stubFor(
                WireMock.get(WireMock.urlPathEqualTo("/product/123/similarids"))
                        .inScenario("Retry Scenario")
                        .whenScenarioStateIs("Second Attempt")
                        .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        )
                        .willSetStateTo("Third Attempt")
        );
        wireMockServer.stubFor(
                WireMock.get(WireMock.urlPathEqualTo("/product/123/similarids"))
                        .inScenario("Retry Scenario")
                        .whenScenarioStateIs("Third Attempt")
                        .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        )
        );

        // WHEN
        List<String> similarProductIds = similarProductsClient.getSimilarProductIds("123");

        // THEN
        assertTrue(similarProductIds.isEmpty());
    }


    @Test
    void shouldReturnSimilarProductIdsOnThirdAttempt() {
        // GIVEN
        wireMockServer.stubFor(
                WireMock.get(WireMock.urlPathEqualTo("/product/123/similarids"))
                        .inScenario("Retry Scenario")
                        .whenScenarioStateIs(Scenario.STARTED)
                        .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        )
                        .willSetStateTo("Second Attempt")
        );
        wireMockServer.stubFor(
                WireMock.get(WireMock.urlPathEqualTo("/product/123/similarids"))
                        .inScenario("Retry Scenario")
                        .whenScenarioStateIs("Second Attempt")
                        .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader("Content-Type", "application/json")
                                .withBody("[\"456\", \"789\"]")
                        )
        );

        // WHEN
        List<String> similarProductIds = similarProductsClient.getSimilarProductIds("123");

        // THEN
        assertFalse(similarProductIds.isEmpty());
        assertEquals(2, similarProductIds.size());
    }

    @Test
    void shouldReturnEmptyProductDetailsAfterThreeFailedAttempts() {
        // GIVEN
        wireMockServer.stubFor(
                WireMock.get(WireMock.urlPathEqualTo("/product/123"))
                        .inScenario("Retry Scenario")
                        .whenScenarioStateIs(Scenario.STARTED)
                        .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        )
                        .willSetStateTo("Second Attempt")
        );
        wireMockServer.stubFor(
                WireMock.get(WireMock.urlPathEqualTo("/product/123"))
                        .inScenario("Retry Scenario")
                        .whenScenarioStateIs("Second Attempt")
                        .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        )
                        .willSetStateTo("Third Attempt")
        );
        wireMockServer.stubFor(
                WireMock.get(WireMock.urlPathEqualTo("/product/123"))
                        .inScenario("Retry Scenario")
                        .whenScenarioStateIs("Third Attempt")
                        .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        )
        );

        // WHEN
        Optional<Product> productDetails = similarProductsClient.getProductDetails("123");

        // THEN
        assertTrue(productDetails.isEmpty());
    }

    @Test
    void shouldReturnProductDetailsOnSecondAttempt() {
        // GIVEN
        wireMockServer.stubFor(
                WireMock.get(WireMock.urlPathEqualTo("/product/123"))
                        .inScenario("Retry Scenario")
                        .whenScenarioStateIs(Scenario.STARTED)
                        .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        )
                        .willSetStateTo("Second Attempt")
        );
        wireMockServer.stubFor(
                WireMock.get(WireMock.urlPathEqualTo("/product/123"))
                        .inScenario("Retry Scenario")
                        .whenScenarioStateIs("Second Attempt")
                        .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader("Content-Type", "application/json")
                                .withBody("{ \"id\": \"123\", \"name\": \"Product 123\" }")
                        )
        );

        // WHEN
        Optional<Product> productDetails = similarProductsClient.getProductDetails("123");

        // THEN
        assertTrue(productDetails.isPresent());
        assertEquals("Product 123", productDetails.get().getName());
    }

}