package com.rutika.inventory.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "Request body for creating or updating a product")
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Schema(description = "Product name", example = "Wireless Mouse", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Product description", example = "Ergonomic wireless mouse with USB receiver")
    private String description;

    @NotBlank(message = "SKU is required")
    @Schema(description = "Stock Keeping Unit (unique identifier)", example = "WM-001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String sku;

    @Positive(message = "Price must be greater than zero")
    @Schema(description = "Product price", example = "29.99")
    private BigDecimal price;

    @Schema(description = "Minimum stock level before reorder is recommended", example = "10")
    private Integer reorderLevel;
}
