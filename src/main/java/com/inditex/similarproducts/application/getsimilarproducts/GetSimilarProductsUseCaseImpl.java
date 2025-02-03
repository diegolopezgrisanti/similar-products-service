package com.inditex.similarproducts.application.getsimilarproducts;

import com.inditex.similarproducts.domain.models.Product;
import com.inditex.similarproducts.domain.ProductRepository;
import com.inditex.similarproducts.domain.exceptions.SimilarProductsFetchingException;
import com.inditex.similarproducts.domain.usecases.GetSimilarProductsUseCase;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class GetSimilarProductsUseCaseImpl implements GetSimilarProductsUseCase {

    private final ProductRepository productRepository;

    public List<Product> getSimilarProducts(String productId) {
        try {
            List<String> similarProductIds = productRepository.getSimilarProductIds(productId);

            return similarProductIds.stream()
                    .map(productRepository::getProductDetails)
                    .flatMap(Optional::stream)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (Exception e) {
            throw new SimilarProductsFetchingException(
                    "Failed to fetch similar products for productId: " + productId);
        }
    }

}