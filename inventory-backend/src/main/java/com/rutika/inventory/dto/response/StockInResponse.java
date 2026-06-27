package com.rutika.inventory.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class StockInResponse {

    private String id;
    private String productId;
    private String productName;
    private String productSku;
    private Integer quantity;
    private String referenceNumber;
    private String notes;
    private Instant createdAt;
}
