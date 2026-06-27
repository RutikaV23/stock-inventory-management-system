package com.rutika.inventory.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request body for removing stock from a product")
public class StockOutRequest {

    @NotBlank(message = "Product ID is required")
    @Schema(description = "ID of the product to remove stock from", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.REQUIRED)
    private String productId;

    @Positive(message = "Quantity must be greater than zero")
    @Schema(description = "Quantity of stock to remove", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;

    @Schema(description = "Reason for stock removal", example = "Customer order fulfillment")
    private String reason;

    @Schema(description = "Optional reference number (e.g., sales order number)", example = "SO-2026-001")
    private String referenceNumber;
}
