package com.rutika.inventory.controller;

import com.rutika.inventory.constants.ApiConstants;
import com.rutika.inventory.constants.MessageConstants;
import com.rutika.inventory.dto.request.StockInRequest;
import com.rutika.inventory.dto.request.StockOutRequest;
import com.rutika.inventory.dto.response.StockInResponse;
import com.rutika.inventory.dto.response.StockOutResponse;
import com.rutika.inventory.response.ApiResponse;
import com.rutika.inventory.service.interfaces.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstants.BASE_PATH + "/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @PostMapping("/in")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<StockInResponse> addStock(@Valid @RequestBody StockInRequest request) {
        StockInResponse response = stockService.addStock(request);
        return ApiResponse.success(MessageConstants.STOCK_IN + MessageConstants.CREATED_SUCCESS, response);
    }

    @PostMapping("/out")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<StockOutResponse> removeStock(@Valid @RequestBody StockOutRequest request) {
        StockOutResponse response = stockService.removeStock(request);
        return ApiResponse.success(MessageConstants.STOCK_OUT + MessageConstants.CREATED_SUCCESS, response);
    }
}
