package com.rutika.inventory.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Schema(description = "Response body containing stock-out transaction details")
public class StockOutResponse {

    @Schema(description = "Unique transaction identifier", example = "770e8400-e29b-41d4-a716-446655440002")
    private String id;

    @Schema(description = "ID of the product", example = "550e8400-e29b-41d4-a716-446655440000")
    private String productId;

    @Schema(description = "Name of the product", example = "Wireless Mouse")
    private String productName;

    @Schema(description = "Quantity removed from stock", example = "5")
    private Integer quantity;

    @Schema(description = "Reason for stock removal", example = "Customer order fulfillment")
    private String reason;

    @Schema(description = "Name of the person who performed the transaction", example = "John Doe")
    private String performedBy;

    @Schema(description = "Timestamp when the transaction was created", example = "2026-06-27T10:30:00Z")
    private Instant createdAt;
}
