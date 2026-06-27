package com.rutika.inventory.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Schema(description = "Response body containing stock-out history item details")
public class StockOutHistoryResponse {

    @Schema(description = "Unique transaction identifier", example = "770e8400-e29b-41d4-a716-446655440002")
    private String id;

    @Schema(description = "ID of the product", example = "550e8400-e29b-41d4-a716-446655440000")
    private String productId;

    @Schema(description = "Name of the product", example = "Wireless Mouse")
    private String productName;

    @Schema(description = "Quantity removed in this transaction", example = "5")
    private Integer quantity;

    @Schema(description = "Current stock quantity after this transaction", example = "145")
    private Integer currentStock;

    @Schema(description = "Reference number (e.g., sales order)", example = "SO-2026-001")
    private String referenceNumber;

    @Schema(description = "Reason for stock removal", example = "Customer order fulfillment")
    private String reason;

    @Schema(description = "Name of the person who performed the transaction (if available)", example = "John Doe")
    private String performedBy;

    @Schema(description = "Date of the stock-out transaction", example = "2026-06-27T10:30:00Z")
    private Instant stockOutDate;

    @Schema(description = "Timestamp when the record was created", example = "2026-06-27T10:30:00Z")
    private Instant createdAt;
}
