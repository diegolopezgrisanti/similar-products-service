package com.inditex.similarproducts.infrastructure.entrypoint.rest;

import com.inditex.similarproducts.application.getsimilarproducts.GetSimilarProductsUseCase;
import com.inditex.similarproducts.domain.Product;
import com.inditex.similarproducts.domain.exceptions.SimilarProductsNotFoundException;
import com.inditex.similarproducts.infrastructure.entrypoint.rest.response.ProductResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final GetSimilarProductsUseCase getSimilarProductsUseCase;

    public ProductController(GetSimilarProductsUseCase getSimilarProductsUseCase) {
        this.getSimilarProductsUseCase = getSimilarProductsUseCase;
    }

    @GetMapping("/{productId}/similar")
    public List<ProductResponseDTO> getSimilarProducts(@PathVariable String productId) {
        List<Product> products = getSimilarProductsUseCase.getSimilarProducts(productId);

        if (products.isEmpty()) {
            throw new SimilarProductsNotFoundException("No similar products found for productId: " + productId);
        }

        return products.stream()
                .map(product -> new ProductResponseDTO(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.isAvailability()
                ))
                .toList();
    }
}