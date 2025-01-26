package com.inditex.similarproducts.infrastructure.entrypoint.rest.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProductResponseDTO {
    private String id;
    private String name;
    private BigDecimal price;
    private boolean availability;
}