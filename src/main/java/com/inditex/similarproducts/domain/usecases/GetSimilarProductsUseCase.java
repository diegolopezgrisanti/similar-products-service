package com.inditex.similarproducts.domain.usecases;

import com.inditex.similarproducts.domain.models.Product;

import java.util.List;

public interface GetSimilarProductsUseCase {
    List<Product> getSimilarProducts(String productId);
}