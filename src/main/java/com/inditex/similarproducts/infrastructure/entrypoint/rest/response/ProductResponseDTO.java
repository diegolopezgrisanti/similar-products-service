package com.inditex.similarproducts.infrastructure.entrypoint.rest.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Schema(description = "Response containing details about similar products")
public class ProductResponseDTO {
    @Schema(
            description = "Unique identifier for the similar product",
            example = "123"
    )
    private String id;

    @Schema(
            description = "Name of the similar product",
            example = "T-shirt"
    )
    private String name;

    @Schema(
            description = "Price of the similar product",
            example = "29.99"
    )
    private BigDecimal price;

    @Schema(
            description = "Availability status of the similar product",
            example = "true"
    )
    private boolean availability;
}