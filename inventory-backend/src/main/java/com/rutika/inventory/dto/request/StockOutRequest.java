package com.rutika.inventory.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Performed by is required")
    @Size(max = 150, message = "Performed by must not exceed 150 characters")
    @Schema(description = "Name of the person performing the stock-out", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String performedBy;

    @Schema(description = "Reason for stock removal", example = "Customer order fulfillment")
    private String reason;
}
