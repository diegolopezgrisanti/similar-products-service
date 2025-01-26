package com.inditex.similarproducts.infrastructure.entrypoint.rest.response.error;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String message;
}