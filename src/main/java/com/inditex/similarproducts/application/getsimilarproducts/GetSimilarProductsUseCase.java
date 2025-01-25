package com.inditex.similarproducts.application.getsimilarproducts;

import com.inditex.similarproducts.domain.Product;
import com.inditex.similarproducts.domain.ProductRepository;
import com.inditex.similarproducts.domain.exceptions.SimilarProductsFetchingException;

import java.util.List;
import java.util.Objects;

public class GetSimilarProductsUseCase {

    private final ProductRepository productRepository;

    public GetSimilarProductsUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getSimilarProducts(String productId) {
        try {
            List<String> similarProductIds = productRepository.getSimilarProductIds(productId);
            return similarProductIds.stream()
                    .map(productRepository::getProductDetails)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (Exception e) {
            throw new SimilarProductsFetchingException(
                    "Failed to fetch similar products for productId: " + productId);
        }
    }

}