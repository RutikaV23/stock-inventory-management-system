package com.rutika.inventory.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Response body containing aggregate dashboard statistics")
public class DashboardStatisticsResponse {

    @Schema(description = "Total number of products in the system", example = "10")
    private long totalProducts;

    @Schema(description = "Number of active products", example = "8")
    private long activeProducts;

    @Schema(description = "Number of inactive products", example = "1")
    private long inactiveProducts;

    @Schema(description = "Number of discontinued products", example = "1")
    private long discontinuedProducts;

    @Schema(description = "Total stock quantity across all products", example = "250")
    private long totalStockQuantity;

    @Schema(description = "Number of products with stock below reorder level", example = "2")
    private long lowStockProducts;

    @Schema(description = "Number of products with zero stock", example = "1")
    private long outOfStockProducts;

    @Schema(description = "Total number of stock-in transactions", example = "45")
    private long totalStockInTransactions;

    @Schema(description = "Total number of stock-out transactions", example = "30")
    private long totalStockOutTransactions;
}
