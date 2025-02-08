package com.inditex.similarproducts.infrastructure.mappers;

import com.inditex.similarproducts.domain.models.Product;
import com.inditex.similarproducts.infrastructure.entrypoint.rest.response.ProductResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponseDTO toResponseDTO(Product product);
}
