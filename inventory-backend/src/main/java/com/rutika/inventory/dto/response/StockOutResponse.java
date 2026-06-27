package com.rutika.inventory.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class StockOutResponse {

    private String id;
    private String productId;
    private String productName;
    private String productSku;
    private Integer quantity;
    private String reason;
    private String referenceNumber;
    private Instant createdAt;
}
