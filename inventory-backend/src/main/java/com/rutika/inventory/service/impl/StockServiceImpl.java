package com.rutika.inventory.service.impl;

import com.rutika.inventory.dto.request.StockInRequest;
import com.rutika.inventory.dto.request.StockOutRequest;
import com.rutika.inventory.dto.response.StockInResponse;
import com.rutika.inventory.dto.response.StockOutResponse;
import com.rutika.inventory.entity.Product;
import com.rutika.inventory.entity.StockIn;
import com.rutika.inventory.entity.StockOut;
import com.rutika.inventory.exception.ResourceNotFoundException;
import com.rutika.inventory.mapper.StockMapper;
import com.rutika.inventory.repository.ProductRepository;
import com.rutika.inventory.repository.StockInRepository;
import com.rutika.inventory.repository.StockOutRepository;
import com.rutika.inventory.service.interfaces.StockService;
import com.rutika.inventory.validator.StockValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockInRepository stockInRepository;
    private final StockOutRepository stockOutRepository;
    private final ProductRepository productRepository;
    private final StockMapper stockMapper;
    private final StockValidator stockValidator;

    @Override
    @Transactional
    public StockInResponse addStock(StockInRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        StockIn stockIn = stockMapper.toInEntity(request);
        stockIn.setProduct(product);

        product.setStockQuantity(product.getStockQuantity() + request.getQuantity());
        productRepository.save(product);

        StockIn savedStockIn = stockInRepository.save(stockIn);
        return stockMapper.toInResponse(savedStockIn);
    }

    @Override
    @Transactional
    public StockOutResponse removeStock(StockOutRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        stockValidator.validateStockOut(request);

        StockOut stockOut = stockMapper.toOutEntity(request);
        stockOut.setProduct(product);

        product.setStockQuantity(product.getStockQuantity() - request.getQuantity());
        productRepository.save(product);

        StockOut savedStockOut = stockOutRepository.save(stockOut);
        return stockMapper.toOutResponse(savedStockOut);
    }
}
