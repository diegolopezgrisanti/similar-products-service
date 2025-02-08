package com.inditex.similarproducts.infrastructure.entrypoint.rest;

import com.inditex.similarproducts.domain.usecases.GetSimilarProductsUseCase;
import com.inditex.similarproducts.infrastructure.entrypoint.rest.response.ProductResponseDTO;
import com.inditex.similarproducts.infrastructure.entrypoint.rest.response.error.ErrorResponse;
import com.inditex.similarproducts.infrastructure.mappers.ProductMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
@Tag(
        name = "Product",
        description = "API for retrieving similar products"
)
@RequiredArgsConstructor
public class ProductController {

    private final GetSimilarProductsUseCase getSimilarProductsUseCase;
    private final ProductMapper productMapper;

    @Operation(
            summary = "Retrieve similar products",
            description = "Returns a list of similar products for a given product ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of similar products successfully retrieved",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ProductResponseDTO.class))
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponse.class,
                                            example = """
                                            {
                                                "message": "No similar products found for productId: 123"
                                            }
                                            """
                                    )
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request due to invalid input or client error",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponse.class,
                                            example = """
                                            {
                                                "message": "ProductId is a required field and cannot be empty."
                                            }
                                            """
                                    )
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponse.class,
                                            example = """
                                            {
                                                "message": "Internal server error, please try later"
                                            }
                                            """
                                    )
                            )
                    }
            )
    })
    @GetMapping("/{productId}/similar")
    public ResponseEntity<List<ProductResponseDTO>> getSimilarProducts(
            @Parameter(
                    description = "The ID of the product to retrieve similar products for",
                    example = "123",
                    required = true
            )
            @PathVariable String productId) {
        List<ProductResponseDTO> similarProducts = getSimilarProductsUseCase.getSimilarProducts(productId)
                .stream()
                .map(productMapper::toResponseDTO)
                .toList();

        return ResponseEntity.ok(similarProducts);
    }
}