package com.rutika.inventory.mapper;

import com.rutika.inventory.dto.request.ProductRequest;
import com.rutika.inventory.dto.response.ProductResponse;
import com.rutika.inventory.entity.Product;
import com.rutika.inventory.util.SentenceCaseUtil;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest request) {
        Product product = new Product();
        product.setName(SentenceCaseUtil.toSentenceCase(request.getName()));
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        return product;
    }

    public ProductResponse toResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStockQuantity(product.getStockQuantity());
        response.setMinimumStock(product.getMinimumStock());
        response.setStatus(product.getStatus().name());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }

    public void updateEntityFromRequest(ProductRequest request, Product product) {
        product.setName(SentenceCaseUtil.toSentenceCase(request.getName()));
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
    }
}
