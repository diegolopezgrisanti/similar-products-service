package com.inditex.similarproducts.application.getsimilarproducts;

import com.inditex.similarproducts.domain.exceptions.SimilarProductsNotFoundException;
import com.inditex.similarproducts.domain.models.Product;
import com.inditex.similarproducts.domain.client.SimilarProductsClient;
import com.inditex.similarproducts.domain.exceptions.SimilarProductsFetchingException;
import com.inditex.similarproducts.domain.usecases.GetSimilarProductsUseCase;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class GetSimilarProductsUseCaseImpl implements GetSimilarProductsUseCase {

    private final SimilarProductsClient similarProductsClient;

    public List<Product> getSimilarProducts(String productId) {
        try {
            List<String> similarProductIds = similarProductsClient.getSimilarProductIds(productId);

            if (similarProductIds.isEmpty()) {
                throw new SimilarProductsNotFoundException("No similar products found for productId: " + productId);
            }

            return similarProductIds.stream()
                    .map(similarProductsClient::getProductDetails)
                    .flatMap(Optional::stream)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (SimilarProductsNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new SimilarProductsFetchingException(
                    "Failed to fetch similar products for productId: " + productId);
        }
    }

}