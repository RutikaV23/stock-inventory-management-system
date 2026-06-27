package com.rutika.inventory.controller;

import com.rutika.inventory.constants.ApiConstants;
import com.rutika.inventory.constants.MessageConstants;
import com.rutika.inventory.dto.response.DashboardStatisticsResponse;
import com.rutika.inventory.response.ApiResponse;
import com.rutika.inventory.service.interfaces.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstants.DASHBOARD_PATH)
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard statistics endpoints")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/statistics")
    @Operation(summary = "Get dashboard statistics",
               description = "Retrieves aggregate statistics for the inventory dashboard including product counts, stock levels, and transaction summaries")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Dashboard statistics retrieved successfully",
                content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                                {
                                    "success": true,
                                    "message": "Dashboard statistics retrieved successfully",
                                    "data": {
                                        "totalProducts": 10,
                                        "activeProducts": 8,
                                        "inactiveProducts": 1,
                                        "discontinuedProducts": 1,
                                        "totalStockQuantity": 250,
                                        "lowStockProducts": 2,
                                        "outOfStockProducts": 1,
                                        "totalStockInTransactions": 45,
                                        "totalStockOutTransactions": 30
                                    },
                                    "timestamp": "2026-06-27T12:00:00Z"
                                }
                                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = @Content(mediaType = "application/json"))
    })
    public ApiResponse<DashboardStatisticsResponse> getStatistics() {
        DashboardStatisticsResponse response = dashboardService.getStatistics();
        return ApiResponse.success(MessageConstants.DASHBOARD + MessageConstants.RETRIEVED_SUCCESS, response);
    }
}
