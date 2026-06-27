package com.rutika.inventory.controller;

import com.rutika.inventory.constants.ApiConstants;
import com.rutika.inventory.constants.MessageConstants;
import com.rutika.inventory.dto.request.StockInRequest;
import com.rutika.inventory.dto.request.StockOutRequest;
import com.rutika.inventory.dto.response.StockInHistoryResponse;
import com.rutika.inventory.dto.response.StockInResponse;
import com.rutika.inventory.dto.response.StockOutResponse;
import com.rutika.inventory.response.ApiResponse;
import com.rutika.inventory.response.PageResponse;
import com.rutika.inventory.service.interfaces.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/in/history")
    @Operation(summary = "Get all Stock In history with pagination, sorting, and search",
               description = "Retrieves a paginated list of Stock In transactions with optional keyword search and sorting")
    @Parameters({
        @Parameter(name = "page", description = "Page number (zero-based)", example = "0"),
        @Parameter(name = "size", description = "Number of items per page", example = "10"),
        @Parameter(name = "sort", description = "Sort field and direction (e.g., stockInDate,desc or quantity,asc)", example = "stockInDate,desc"),
        @Parameter(name = "keyword", description = "Search keyword (matches product name, SKU, reference number, or remarks)", example = "laptop")
    })
    public ApiResponse<PageResponse<StockInHistoryResponse>> getStockInHistory(
            @RequestParam(defaultValue = ApiConstants.PAGE_DEFAULT) int page,
            @RequestParam(defaultValue = ApiConstants.SIZE_DEFAULT) int size,
            @RequestParam(defaultValue = ApiConstants.STOCK_IN_HISTORY_SORT_DEFAULT) String sort,
            @RequestParam(required = false) String keyword) {
        PageResponse<StockInHistoryResponse> response = stockService.getStockInHistory(page, size, sort, keyword);
        return ApiResponse.success(MessageConstants.STOCK_IN + MessageConstants.RETRIEVED_SUCCESS, response);
    }
}
