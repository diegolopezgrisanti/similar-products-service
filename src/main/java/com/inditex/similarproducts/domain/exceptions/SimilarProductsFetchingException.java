package com.inditex.similarproducts.domain.exceptions;

public class SimilarProductsFetchingException extends RuntimeException {
    public SimilarProductsFetchingException(String message) {
        super(message);
    }
}