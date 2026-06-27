package com.rutika.inventory.service.impl;

import com.rutika.inventory.dto.request.StockInRequest;
import com.rutika.inventory.dto.request.StockOutRequest;
import com.rutika.inventory.dto.response.StockInHistoryResponse;
import com.rutika.inventory.dto.response.StockInResponse;
import com.rutika.inventory.dto.response.StockOutResponse;
import com.rutika.inventory.entity.Product;
import com.rutika.inventory.entity.StockIn;
import com.rutika.inventory.entity.StockOut;
import com.rutika.inventory.exception.BadRequestException;
import com.rutika.inventory.exception.ResourceNotFoundException;
import com.rutika.inventory.mapper.StockMapper;
import com.rutika.inventory.repository.ProductRepository;
import com.rutika.inventory.repository.StockInRepository;
import com.rutika.inventory.repository.StockOutRepository;
import com.rutika.inventory.response.PageResponse;
import com.rutika.inventory.service.interfaces.StockService;
import com.rutika.inventory.validator.StockValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Override
    @Transactional(readOnly = true)
    public PageResponse<StockInHistoryResponse> getStockInHistory(int page, int size, String sort, String keyword) {
        if (page < 0) {
            throw new BadRequestException("Page number cannot be negative");
        }
        if (size < 1) {
            throw new BadRequestException("Page size must be greater than zero");
        }

        String[] sortParams = sort.split(",");
        String sortBy = sortParams[0];
        Sort.Direction sortDir = Sort.Direction.DESC;
        if (sortParams.length > 1) {
            try {
                sortDir = Sort.Direction.fromString(sortParams[1]);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid sort direction: " + sortParams[1] + ". Use 'asc' or 'desc'.");
            }
        }

        String entitySortField = mapSortField(sortBy);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDir, entitySortField));

        String trimmedKeyword = keyword != null ? keyword.trim() : null;

        Page<StockIn> stockInPage;
        if (trimmedKeyword != null && !trimmedKeyword.isBlank()) {
            stockInPage = stockInRepository.searchByKeyword(trimmedKeyword, pageable);
        } else {
            stockInPage = stockInRepository.findAll(pageable);
        }

        return PageResponse.<StockInHistoryResponse>builder()
                .content(stockInPage.getContent().stream()
                        .map(stockMapper::toHistoryResponse)
                        .toList())
                .page(stockInPage.getNumber())
                .size(stockInPage.getSize())
                .totalElements(stockInPage.getTotalElements())
                .totalPages(stockInPage.getTotalPages())
                .first(stockInPage.isFirst())
                .last(stockInPage.isLast())
                .build();
    }

    private String mapSortField(String sortField) {
        if ("stockInDate".equals(sortField)) {
            return "createdAt";
        }
        return sortField;
    }
}
