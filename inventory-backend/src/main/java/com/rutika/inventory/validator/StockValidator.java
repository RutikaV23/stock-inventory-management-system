package com.rutika.inventory.validator;

import com.rutika.inventory.dto.request.StockOutRequest;
import com.rutika.inventory.entity.Product;
import com.rutika.inventory.exception.ValidationException;
import com.rutika.inventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockValidator {

    private final ProductRepository productRepository;

    public void validateStockOut(StockOutRequest request) {
        Product product = productRepository.findById(request.getProductId()).orElse(null);
        if (product == null) {
            return;
        }
        if (product.getStockQuantity() < request.getQuantity()) {
            throw new ValidationException(
                    "Insufficient stock. Available: " + product.getStockQuantity()
                            + ", requested: " + request.getQuantity());
        }
    }
}
