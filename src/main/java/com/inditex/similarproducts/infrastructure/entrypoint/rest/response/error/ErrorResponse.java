package com.inditex.similarproducts.infrastructure.entrypoint.rest.response.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Response returned when an error occurs in the API")
public class ErrorResponse {

    @Schema(
            description = "Detailed error message explaining the cause of the issue",
            example = "No similar products found for productId: 123"
    )
    private String message;
}