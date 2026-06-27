package com.rutika.inventory.service.interfaces;

import com.rutika.inventory.dto.request.StockInRequest;
import com.rutika.inventory.dto.request.StockOutRequest;
import com.rutika.inventory.dto.response.StockInHistoryResponse;
import com.rutika.inventory.dto.response.StockInResponse;
import com.rutika.inventory.dto.response.StockOutResponse;
import com.rutika.inventory.response.PageResponse;

public interface StockService {

    StockInResponse addStock(StockInRequest request);

    StockOutResponse removeStock(StockOutRequest request);

    PageResponse<StockInHistoryResponse> getStockInHistory(int page, int size, String sort, String keyword);
}
