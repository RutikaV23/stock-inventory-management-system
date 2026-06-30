package com.rutika.inventory.mapper;

import com.rutika.inventory.dto.request.StockInRequest;
import com.rutika.inventory.dto.request.StockOutRequest;
import com.rutika.inventory.dto.response.StockInHistoryResponse;
import com.rutika.inventory.dto.response.StockInResponse;
import com.rutika.inventory.dto.response.StockOutHistoryResponse;
import com.rutika.inventory.dto.response.StockOutResponse;
import com.rutika.inventory.entity.StockIn;
import com.rutika.inventory.entity.StockOut;
import org.springframework.stereotype.Component;

@Component
public class StockMapper {

    public StockIn toInEntity(StockInRequest request) {
        StockIn stockIn = new StockIn();
        stockIn.setQuantity(request.getQuantity());
        stockIn.setPerformedBy(request.getPerformedBy());
        stockIn.setNotes(request.getNotes());
        return stockIn;
    }

    public StockInResponse toInResponse(StockIn stockIn) {
        StockInResponse response = new StockInResponse();
        response.setId(stockIn.getId());
        response.setProductId(stockIn.getProduct().getId());
        response.setProductName(stockIn.getProduct().getName());
        response.setQuantity(stockIn.getQuantity());
        response.setPerformedBy(stockIn.getPerformedBy());
        response.setNotes(stockIn.getNotes());
        response.setCreatedAt(stockIn.getCreatedAt());
        return response;
    }

    public StockOut toOutEntity(StockOutRequest request) {
        StockOut stockOut = new StockOut();
        stockOut.setQuantity(request.getQuantity());
        stockOut.setPerformedBy(request.getPerformedBy());
        stockOut.setReason(request.getReason());
        return stockOut;
    }

    public StockOutResponse toOutResponse(StockOut stockOut) {
        StockOutResponse response = new StockOutResponse();
        response.setId(stockOut.getId());
        response.setProductId(stockOut.getProduct().getId());
        response.setProductName(stockOut.getProduct().getName());
        response.setQuantity(stockOut.getQuantity());
        response.setReason(stockOut.getReason());
        response.setPerformedBy(stockOut.getPerformedBy());
        response.setCreatedAt(stockOut.getCreatedAt());
        return response;
    }

    public StockInHistoryResponse toHistoryResponse(StockIn stockIn) {
        StockInHistoryResponse response = new StockInHistoryResponse();
        response.setId(stockIn.getId());
        response.setProductId(stockIn.getProduct().getId());
        response.setProductName(stockIn.getProduct().getName());
        response.setQuantity(stockIn.getQuantity());
        response.setCurrentStock(stockIn.getProduct().getStockQuantity());
        response.setPerformedBy(stockIn.getPerformedBy());
        response.setNotes(stockIn.getNotes());
        response.setStockInDate(stockIn.getCreatedAt());
        response.setCreatedAt(stockIn.getCreatedAt());
        return response;
    }

    public StockOutHistoryResponse toHistoryResponse(StockOut stockOut) {
        StockOutHistoryResponse response = new StockOutHistoryResponse();
        response.setId(stockOut.getId());
        response.setProductId(stockOut.getProduct().getId());
        response.setProductName(stockOut.getProduct().getName());
        response.setQuantity(stockOut.getQuantity());
        response.setCurrentStock(stockOut.getProduct().getStockQuantity());
        response.setPerformedBy(stockOut.getPerformedBy());
        response.setReason(stockOut.getReason());
        response.setStockOutDate(stockOut.getCreatedAt());
        response.setCreatedAt(stockOut.getCreatedAt());
        return response;
    }
}
