package com.rutika.inventory.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
public class ProductResponse {

    private String id;
    private String name;
    private String description;
    private String sku;
    private BigDecimal price;
    private Integer stockQuantity;
    private String categoryName;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;
}
