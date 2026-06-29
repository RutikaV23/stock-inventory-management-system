package com.rutika.inventory.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Schema(description = "Response body containing product details")
public class ProductResponse {

    @Schema(description = "Unique product identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    private String id;

    @Schema(description = "Product name", example = "Wireless Mouse")
    private String name;

    @Schema(description = "Product description", example = "Ergonomic wireless mouse with USB receiver")
    private String description;

    @Schema(description = "Product price", example = "29.99")
    private BigDecimal price;

    @Schema(description = "Current stock quantity", example = "150")
    private Integer stockQuantity;

    @Schema(description = "Minimum stock level before reorder is recommended", example = "2")
    private Integer minimumStock;

    @Schema(description = "Product status", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "DISCONTINUED"})
    private String status;

    @Schema(description = "Timestamp when the product was created", example = "2026-06-27T10:30:00Z")
    private Instant createdAt;

    @Schema(description = "Timestamp when the product was last updated", example = "2026-06-27T10:30:00Z")
    private Instant updatedAt;
}
