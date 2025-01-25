package com.inditex.similarproducts.infrastructure.client;

import com.inditex.similarproducts.domain.Product;
import com.inditex.similarproducts.domain.ProductRepository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SimilarProductsClient implements ProductRepository {

    private final RestTemplate restTemplate;
    private final String similarProductsUrl;

    public SimilarProductsClient(RestTemplate restTemplate, String similarProductsUrl) {
        this.restTemplate = restTemplate;
        this.similarProductsUrl = similarProductsUrl;
    }

    @Override
    public List<String> getSimilarProductIds(String productId) {
        // Call the similarIds endpoint
        String similarIdsUrl = UriComponentsBuilder
                .fromUriString(similarProductsUrl)
                .pathSegment("product", productId, "similarids")
                .toUriString();

        try {
            String[] similarIds = restTemplate.getForObject(similarIdsUrl, String[].class);

            return similarIds != null ? Arrays.asList(similarIds) : List.of();
        } catch (HttpClientErrorException.NotFound e) {

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

            return Optional.ofNullable(restTemplate.getForObject(productDetailUrl, Product.class));
        } catch (HttpClientErrorException.NotFound e) {

        return Optional.empty();
        }
    }

}