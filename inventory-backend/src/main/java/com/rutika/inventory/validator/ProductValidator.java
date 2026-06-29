package com.rutika.inventory.validator;

import com.rutika.inventory.dto.request.ProductRequest;
import org.springframework.stereotype.Component;

@Component
public class ProductValidator {

    public void validateCreate(ProductRequest request) {
    }

    public void validateUpdate(String id, ProductRequest request) {
    }
}
