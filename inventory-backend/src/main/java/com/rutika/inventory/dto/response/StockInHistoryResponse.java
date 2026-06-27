package com.rutika.inventory.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class StockInHistoryResponse {

    private String id;
    private String productId;
    private String productName;
    private String sku;
    private Integer quantity;
    private Integer currentStock;
    private String supplierName;
    private String performedBy;
    private Instant stockInDate;
    private String remarks;
    private Instant createdAt;
}
