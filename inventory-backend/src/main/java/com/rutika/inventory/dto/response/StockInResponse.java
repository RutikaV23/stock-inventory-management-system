package com.rutika.inventory.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Schema(description = "Response body containing stock-in transaction details")
public class StockInResponse {

    @Schema(description = "Unique transaction identifier", example = "660e8400-e29b-41d4-a716-446655440001")
    private String id;

    @Schema(description = "ID of the product", example = "550e8400-e29b-41d4-a716-446655440000")
    private String productId;

    @Schema(description = "Name of the product", example = "Wireless Mouse")
    private String productName;

    @Schema(description = "Quantity added to stock", example = "50")
    private Integer quantity;

    @Schema(description = "Name of the person who performed the transaction", example = "John Doe")
    private String performedBy;

    @Schema(description = "Notes about the transaction", example = "Restock from supplier")
    private String notes;

    @Schema(description = "Timestamp when the transaction was created", example = "2026-06-27T10:30:00Z")
    private Instant createdAt;
}
