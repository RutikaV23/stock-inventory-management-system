package com.rutika.inventory.service.impl;

import com.rutika.inventory.dto.response.DashboardStatisticsResponse;
import com.rutika.inventory.enums.ProductStatus;
import com.rutika.inventory.repository.ProductRepository;
import com.rutika.inventory.repository.StockInRepository;
import com.rutika.inventory.repository.StockOutRepository;
import com.rutika.inventory.service.interfaces.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ProductRepository productRepository;
    private final StockInRepository stockInRepository;
    private final StockOutRepository stockOutRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardStatisticsResponse getStatistics() {
        long totalProducts = productRepository.count();
        long activeProducts = productRepository.countByStatus(ProductStatus.ACTIVE);
        long inactiveProducts = productRepository.countByStatus(ProductStatus.INACTIVE);
        long discontinuedProducts = productRepository.countByStatus(ProductStatus.DISCONTINUED);
        long totalStockQuantity = productRepository.sumStockQuantity();
        long lowStockProducts = productRepository.countLowStockProducts();
        long outOfStockProducts = productRepository.countOutOfStockProducts();
        long totalStockInTransactions = stockInRepository.count();
        long totalStockOutTransactions = stockOutRepository.count();

        return DashboardStatisticsResponse.builder()
                .totalProducts(totalProducts)
                .activeProducts(activeProducts)
                .inactiveProducts(inactiveProducts)
                .discontinuedProducts(discontinuedProducts)
                .totalStockQuantity(totalStockQuantity)
                .lowStockProducts(lowStockProducts)
                .outOfStockProducts(outOfStockProducts)
                .totalStockInTransactions(totalStockInTransactions)
                .totalStockOutTransactions(totalStockOutTransactions)
                .build();
    }
}
