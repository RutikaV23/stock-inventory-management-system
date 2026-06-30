package com.rutika.inventory.controller;

import com.rutika.inventory.constants.ApiConstants;
import com.rutika.inventory.constants.MessageConstants;
import com.rutika.inventory.dto.request.StockInRequest;
import com.rutika.inventory.dto.request.StockOutRequest;
import com.rutika.inventory.dto.response.StockInHistoryResponse;
import com.rutika.inventory.dto.response.StockInResponse;
import com.rutika.inventory.dto.response.StockOutHistoryResponse;
import com.rutika.inventory.dto.response.StockOutResponse;
import com.rutika.inventory.response.ApiResponse;
import com.rutika.inventory.response.PageResponse;
import com.rutika.inventory.service.interfaces.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Stock", description = "Stock transaction endpoints for stock-in, stock-out, and history")
public class StockController {

    private final StockService stockService;

    @PostMapping("/in")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add stock to a product",
               description = "Records a stock-in transaction and increases the product's stock quantity")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "Stock added successfully",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiResponse.class),
                        examples = @ExampleObject(value = """
                                {
                                    "success": true,
                                    "message": "StockIn created successfully",
                                    "data": {
                                        "id": "660e8400-e29b-41d4-a716-446655440001",
                                        "productId": "550e8400-e29b-41d4-a716-446655440000",
                                        "productName": "Wireless Mouse",
                                        "quantity": 50,
                                        "performedBy": "John Doe",
                                        "notes": "Restock from supplier",
                                        "createdAt": "2026-06-27T10:30:00Z"
                                    },
                                    "timestamp": "2026-06-27T10:30:00Z"
                                }
                                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "Invalid request body (validation error)",
                content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                                {
                                    "success": false,
                                    "message": "Validation failed",
                                    "data": null,
                                    "timestamp": "2026-06-27T10:30:00Z"
                                }
                                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Product not found",
                content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                                {
                                    "success": false,
                                    "message": "Product not found with id: 550e8400-e29b-41d4-a716-446655440000",
                                    "data": null,
                                    "timestamp": "2026-06-27T10:30:00Z"
                                }
                                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = @Content(mediaType = "application/json"))
    })
    public ApiResponse<StockInResponse> addStock(@Valid @RequestBody StockInRequest request) {
        StockInResponse response = stockService.addStock(request);
        return ApiResponse.success(MessageConstants.STOCK_IN + MessageConstants.CREATED_SUCCESS, response);
    }

    @PostMapping("/out")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Remove stock from a product",
               description = "Records a stock-out transaction and decreases the product's stock quantity")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "Stock removed successfully",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiResponse.class),
                        examples = @ExampleObject(value = """
                                {
                                    "success": true,
                                    "message": "StockOut created successfully",
                                    "data": {
                                        "id": "770e8400-e29b-41d4-a716-446655440002",
                                        "productId": "550e8400-e29b-41d4-a716-446655440000",
                                        "productName": "Wireless Mouse",
                                        "quantity": 5,
                                        "reason": "Customer order fulfillment",
                                        "performedBy": "John Doe",
                                        "createdAt": "2026-06-27T10:30:00Z"
                                    },
                                    "timestamp": "2026-06-27T10:30:00Z"
                                }
                                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "Invalid request body (validation error)",
                content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                                {
                                    "success": false,
                                    "message": "Validation failed",
                                    "data": null,
                                    "timestamp": "2026-06-27T10:30:00Z"
                                }
                                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "Product not found",
                content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                                {
                                    "success": false,
                                    "message": "Product not found with id: 550e8400-e29b-41d4-a716-446655440000",
                                    "data": null,
                                    "timestamp": "2026-06-27T10:30:00Z"
                                }
                                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "409",
                description = "Insufficient stock quantity",
                content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                                {
                                    "success": false,
                                    "message": "Insufficient stock. Available: 10, Requested: 50",
                                    "data": null,
                                    "timestamp": "2026-06-27T10:30:00Z"
                                }
                                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = @Content(mediaType = "application/json"))
    })
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
        @Parameter(name = "keyword", description = "Search keyword (matches product name, performed by, or remarks)", example = "mouse")
    })
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Stock In history retrieved successfully",
                content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                                {
                                    "success": true,
                                    "message": "StockIn retrieved successfully",
                                    "data": {
                                        "content": [
                                            {
                                                "id": "660e8400-e29b-41d4-a716-446655440001",
                                                "productId": "550e8400-e29b-41d4-a716-446655440000",
                                                "productName": "Wireless Mouse",
                                                "quantity": 50,
                                                "currentStock": 150,
                                                "performedBy": "John Doe",
                                                "notes": "Restock from supplier",
                                                "stockInDate": "2026-06-27T10:30:00Z",
                                                "createdAt": "2026-06-27T10:30:00Z"
                                            }
                                        ],
                                        "page": 0,
                                        "size": 10,
                                        "totalElements": 1,
                                        "totalPages": 1,
                                        "first": true,
                                        "last": true
                                    },
                                    "timestamp": "2026-06-27T10:30:00Z"
                                }
                                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = @Content(mediaType = "application/json"))
    })
    public ApiResponse<PageResponse<StockInHistoryResponse>> getStockInHistory(
            @RequestParam(defaultValue = ApiConstants.PAGE_DEFAULT) int page,
            @RequestParam(defaultValue = ApiConstants.SIZE_DEFAULT) int size,
            @RequestParam(defaultValue = ApiConstants.STOCK_IN_HISTORY_SORT_DEFAULT) String sort,
            @RequestParam(required = false) String keyword) {
        PageResponse<StockInHistoryResponse> response = stockService.getStockInHistory(page, size, sort, keyword);
        return ApiResponse.success(MessageConstants.STOCK_IN + MessageConstants.RETRIEVED_SUCCESS, response);
    }

    @GetMapping("/out/history")
    @Operation(summary = "Get all Stock Out history with pagination, sorting, and search",
               description = "Retrieves a paginated list of Stock Out transactions with optional keyword search and sorting")
    @Parameters({
        @Parameter(name = "page", description = "Page number (zero-based)", example = "0"),
        @Parameter(name = "size", description = "Number of items per page", example = "10"),
        @Parameter(name = "sort", description = "Sort field and direction (e.g., stockOutDate,desc or quantity,asc)", example = "stockOutDate,desc"),
        @Parameter(name = "keyword", description = "Search keyword (matches product name, performed by, or reason)", example = "mouse")
    })
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Stock Out history retrieved successfully",
                content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                                {
                                    "success": true,
                                    "message": "StockOut retrieved successfully",
                                    "data": {
                                        "content": [
                                            {
                                                "id": "770e8400-e29b-41d4-a716-446655440002",
                                                "productId": "550e8400-e29b-41d4-a716-446655440000",
                                                "productName": "Wireless Mouse",
                                                "quantity": 5,
                                                "currentStock": 145,
                                                "performedBy": "John Doe",
                                                "reason": "Customer order fulfillment",
                                                "stockOutDate": "2026-06-27T10:30:00Z",
                                                "createdAt": "2026-06-27T10:30:00Z"
                                            }
                                        ],
                                        "page": 0,
                                        "size": 10,
                                        "totalElements": 1,
                                        "totalPages": 1,
                                        "first": true,
                                        "last": true
                                    },
                                    "timestamp": "2026-06-27T10:30:00Z"
                                }
                                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = @Content(mediaType = "application/json"))
    })
    public ApiResponse<PageResponse<StockOutHistoryResponse>> getStockOutHistory(
            @RequestParam(defaultValue = ApiConstants.PAGE_DEFAULT) int page,
            @RequestParam(defaultValue = ApiConstants.SIZE_DEFAULT) int size,
            @RequestParam(defaultValue = ApiConstants.STOCK_OUT_HISTORY_SORT_DEFAULT) String sort,
            @RequestParam(required = false) String keyword) {
        PageResponse<StockOutHistoryResponse> response = stockService.getStockOutHistory(page, size, sort, keyword);
        return ApiResponse.success(MessageConstants.STOCK_OUT + MessageConstants.RETRIEVED_SUCCESS, response);
    }
}
