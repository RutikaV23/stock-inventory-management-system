package com.rutika.inventory.service.interfaces;

import com.rutika.inventory.dto.request.StockInRequest;
import com.rutika.inventory.dto.request.StockOutRequest;
import com.rutika.inventory.dto.response.StockInHistoryResponse;
import com.rutika.inventory.dto.response.StockInResponse;
import com.rutika.inventory.dto.response.StockOutHistoryResponse;
import com.rutika.inventory.dto.response.StockOutResponse;
import com.rutika.inventory.response.PageResponse;

public interface StockService {

    StockInResponse addStock(StockInRequest request);

    StockOutResponse removeStock(StockOutRequest request);

    StockInResponse updateStockIn(String id, StockInRequest request);

    StockOutResponse updateStockOut(String id, StockOutRequest request);

    void deleteStockIn(String id);

    void deleteStockOut(String id);

    PageResponse<StockInHistoryResponse> getStockInHistory(int page, int size, String sort, String keyword);

    PageResponse<StockOutHistoryResponse> getStockOutHistory(int page, int size, String sort, String keyword);
}
