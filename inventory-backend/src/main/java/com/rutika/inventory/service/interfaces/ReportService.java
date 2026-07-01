package com.rutika.inventory.service.interfaces;

import com.rutika.inventory.dto.response.ProductReportResponse;
import com.rutika.inventory.response.PageResponse;

import java.time.Instant;

public interface ReportService {

    PageResponse<ProductReportResponse> getProductReports(int page, int size, String keyword, String status, Instant dateFrom, Instant dateTo);

    byte[] exportProductReportsToExcel(String keyword, String status, Instant dateFrom, Instant dateTo);
}
