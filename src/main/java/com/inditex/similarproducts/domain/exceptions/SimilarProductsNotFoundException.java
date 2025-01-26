package com.inditex.similarproducts.domain.exceptions;

public class SimilarProductsNotFoundException extends RuntimeException {
    public SimilarProductsNotFoundException(String message) {
        super(message);
    }
}