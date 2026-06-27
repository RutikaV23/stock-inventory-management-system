package com.rutika.inventory.validator;

import com.rutika.inventory.dto.request.ProductRequest;
import com.rutika.inventory.exception.ValidationException;
import com.rutika.inventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductValidator {

    private final ProductRepository productRepository;

    public void validateCreate(ProductRequest request) {
        Map<String, String> errors = new HashMap<>();

        if (productRepository.existsBySku(request.getSku())) {
            errors.put("sku", "Product with SKU " + request.getSku() + " already exists");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    public void validateUpdate(String id, ProductRequest request) {
        Map<String, String> errors = new HashMap<>();

        productRepository.findBySku(request.getSku()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                errors.put("sku", "Product with SKU " + request.getSku() + " already exists");
            }
        });

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
