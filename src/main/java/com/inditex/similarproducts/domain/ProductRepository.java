package com.inditex.similarproducts.domain;

import java.util.List;

public interface ProductRepository {

    List<String> getSimilarProductIds(String productId);

    Product getProductDetails(String productId);
}