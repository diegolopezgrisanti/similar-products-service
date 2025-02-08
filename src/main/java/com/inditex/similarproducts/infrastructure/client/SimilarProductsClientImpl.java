package com.inditex.similarproducts.infrastructure.client;

import com.inditex.similarproducts.domain.client.SimilarProductsClient;
import com.inditex.similarproducts.domain.models.Product;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class SimilarProductsClientImpl implements SimilarProductsClient {

    private final RestTemplate restTemplate;
    private final String similarProductsUrl;
    private final Retry retry;

    @Override
    public List<String> getSimilarProductIds(String productId) {
        // Call the similarIds endpoint
        String similarIdsUrl = UriComponentsBuilder
                .fromUriString(similarProductsUrl)
                .pathSegment("product", productId, "similarids")
                .toUriString();

        try {
            return Retry.decorateCheckedSupplier(retry, () -> {
                String[] similarIds = restTemplate.getForObject(similarIdsUrl, String[].class);
                return similarIds != null ? Arrays.asList(similarIds) : List.<String>of();
            }).get();
        } catch (Throwable throwable) {
            return List.of();
        }
    }

    @Override
    public Optional<Product> getProductDetails(String productId) {
        // Call the product detail endpoint
        String productDetailUrl = UriComponentsBuilder
                .fromUriString(similarProductsUrl)
                .pathSegment("product", productId)
                .toUriString();

        try {
            return Retry.decorateCheckedSupplier(retry, () -> {
                Product product = restTemplate.getForObject(productDetailUrl, Product.class);
                return Optional.ofNullable(product);
            }).get();
        } catch (Throwable e) {
            return Optional.empty();
        }
    }
}