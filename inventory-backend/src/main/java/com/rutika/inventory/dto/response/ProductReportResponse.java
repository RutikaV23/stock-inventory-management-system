package com.rutika.inventory.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "Response body containing product report data")
public class ProductReportResponse {

    @Schema(description = "Unique product identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    private String productId;

    @Schema(description = "Product name", example = "Wireless Mouse")
    private String productName;

    @Schema(description = "Current stock quantity", example = "150")
    private Integer currentStock;

    @Schema(description = "Product price", example = "29.99")
    private BigDecimal price;

    @Schema(description = "Total stock-in quantity", example = "200")
    private Integer totalStockIn;

    @Schema(description = "Total stock-out quantity", example = "50")
    private Integer totalStockOut;

    @Schema(description = "Available stock (totalStockIn - totalStockOut)", example = "150")
    private Integer availableStock;

    @Schema(description = "Inventory value (currentStock * price)", example = "4485.00")
    private BigDecimal inventoryValue;

    @Schema(description = "Product status", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "DISCONTINUED"})
    private String status;
}
