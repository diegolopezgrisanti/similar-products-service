package com.inditex.similarproducts.domain;

import com.inditex.similarproducts.domain.models.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    List<String> getSimilarProductIds(String productId);

    Optional<Product> getProductDetails(String productId);
}