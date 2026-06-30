package com.rutika.inventory.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request body for adding stock to a product")
public class StockInRequest {

    @NotBlank(message = "Product ID is required")
    @Schema(description = "ID of the product to add stock to", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.REQUIRED)
    private String productId;

    @Positive(message = "Quantity must be greater than zero")
    @Schema(description = "Quantity of stock to add", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;

    @NotBlank(message = "Performed by is required")
    @Size(max = 150, message = "Performed by must not exceed 150 characters")
    @Schema(description = "Name of the person performing the stock-in", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String performedBy;

    @Schema(description = "Optional notes or remarks about the stock-in transaction", example = "Restock from supplier")
    private String notes;
}
