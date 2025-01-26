package com.inditex.similarproducts.infrastructure.entrypoint.rest;

import com.inditex.similarproducts.application.getsimilarproducts.GetSimilarProductsUseCase;
import com.inditex.similarproducts.domain.Product;
import com.inditex.similarproducts.domain.exceptions.SimilarProductsNotFoundException;
import com.inditex.similarproducts.infrastructure.entrypoint.rest.response.ProductResponseDTO;
import com.inditex.similarproducts.infrastructure.entrypoint.rest.response.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
public class ProductController {

    private final GetSimilarProductsUseCase getSimilarProductsUseCase;

    public ProductController(GetSimilarProductsUseCase getSimilarProductsUseCase) {
        this.getSimilarProductsUseCase = getSimilarProductsUseCase;
    }

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
    public List<ProductResponseDTO> getSimilarProducts(
            @Parameter(
                    description = "The ID of the product to retrieve similar products for",
                    example = "123",
                    required = true
            )
            @PathVariable String productId) {
        List<Product> products = getSimilarProductsUseCase.getSimilarProducts(productId);

        if (products.isEmpty()) {
            throw new SimilarProductsNotFoundException("No similar products found for productId: " + productId);
        }

        return products.stream()
                .map(product -> new ProductResponseDTO(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.isAvailability()
                ))
                .toList();
    }
}