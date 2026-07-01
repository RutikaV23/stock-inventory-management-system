package com.rutika.inventory.controller;

import com.rutika.inventory.dto.response.ProductReportResponse;
import com.rutika.inventory.response.ApiResponse;
import com.rutika.inventory.response.PageResponse;
import com.rutika.inventory.service.interfaces.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Read-only inventory reports with pagination, filtering, and Excel export")
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    @Operation(summary = "Get inventory reports",
               description = "Retrieves paginated inventory reports with optional keyword search, status filter, and date range")
    @Parameters({
        @Parameter(name = "page", description = "Page number (zero-based)", example = "0"),
        @Parameter(name = "size", description = "Number of items per page", example = "10"),
        @Parameter(name = "keyword", description = "Search keyword (matches product name)", example = "mouse"),
        @Parameter(name = "status", description = "Filter by product status", example = "ACTIVE"),
        @Parameter(name = "dateFrom", description = "Start date for stock activity filter (ISO format)", example = "2026-01-01T00:00:00Z"),
        @Parameter(name = "dateTo", description = "End date for stock activity filter (ISO format)", example = "2026-12-31T23:59:59Z")
    })
    public ApiResponse<PageResponse<ProductReportResponse>> getProductReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dateTo) {
        PageResponse<ProductReportResponse> response = reportService.getProductReports(
                page, size, keyword, status, dateFrom, dateTo);
        return ApiResponse.success("Reports retrieved successfully", response);
    }

    @GetMapping("/export/excel")
    @Operation(summary = "Export inventory reports to Excel",
               description = "Exports inventory reports as an Excel (.xlsx) file with columns: Product Name, Current Stock, Total Stock In, Total Stock Out, Available Stock, Inventory Value, Status")
    public ResponseEntity<byte[]> exportProductReportsToExcel(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dateTo) {
        byte[] excelData = reportService.exportProductReportsToExcel(keyword, status, dateFrom, dateTo);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventory-reports.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelData);
    }
}
