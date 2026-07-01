package com.rutika.inventory.service.impl;

import com.rutika.inventory.dto.response.ProductReportResponse;
import com.rutika.inventory.entity.Product;
import com.rutika.inventory.enums.ProductStatus;
import com.rutika.inventory.repository.ProductRepository;
import com.rutika.inventory.repository.StockInRepository;
import com.rutika.inventory.repository.StockOutRepository;
import com.rutika.inventory.response.PageResponse;
import com.rutika.inventory.service.interfaces.ReportService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ProductRepository productRepository;
    private final StockInRepository stockInRepository;
    private final StockOutRepository stockOutRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductReportResponse> getProductReports(int page, int size, String keyword, String status, Instant dateFrom, Instant dateTo) {
        if (page < 0) {
            page = 0;
        }
        if (size < 1) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));

        boolean hasKeyword = keyword != null && !keyword.isBlank();
        boolean hasStatus = status != null && !status.isBlank();

        Page<Product> productPage;
        if (hasKeyword && hasStatus) {
            productPage = productRepository
                    .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStatus(
                            keyword, keyword, ProductStatus.valueOf(status.toUpperCase()), pageable);
        } else if (hasStatus) {
            productPage = productRepository
                    .findByStatus(ProductStatus.valueOf(status.toUpperCase()), pageable);
        } else if (hasKeyword) {
            productPage = productRepository
                    .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                            keyword, keyword, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        List<ProductReportResponse> reports = productPage.getContent().stream()
                .map(product -> buildReport(product, dateFrom, dateTo))
                .toList();

        return PageResponse.<ProductReportResponse>builder()
                .content(reports)
                .page(productPage.getNumber())
                .size(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .first(productPage.isFirst())
                .last(productPage.isLast())
                .build();
    }

    private ProductReportResponse buildReport(Product product, Instant dateFrom, Instant dateTo) {
        ProductReportResponse report = new ProductReportResponse();
        report.setProductId(product.getId());
        report.setProductName(product.getName());
        report.setCurrentStock(product.getStockQuantity());
        report.setPrice(product.getPrice());
        report.setStatus(product.getStatus().name());

        Integer totalIn = stockInRepository.sumQuantityByProductIdAndDateBetween(
                product.getId(), dateFrom, dateTo);
        Integer totalOut = stockOutRepository.sumQuantityByProductIdAndDateBetween(
                product.getId(), dateFrom, dateTo);

        int totalStockIn = totalIn != null ? totalIn : 0;
        int totalStockOut = totalOut != null ? totalOut : 0;

        report.setTotalStockIn(totalStockIn);
        report.setTotalStockOut(totalStockOut);
        report.setAvailableStock(Math.max(0, totalStockIn - totalStockOut));

        BigDecimal price = product.getPrice();
        if (price != null) {
            report.setInventoryValue(price.multiply(BigDecimal.valueOf(product.getStockQuantity() != null ? product.getStockQuantity() : 0)));
        } else {
            report.setInventoryValue(BigDecimal.ZERO);
        }

        return report;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportProductReportsToExcel(String keyword, String status, Instant dateFrom, Instant dateTo) {
        List<Product> allProducts = getAllProducts(keyword, status);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Inventory Reports");

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            CellStyle currencyStyle = workbook.createCellStyle();
            currencyStyle.setDataFormat(workbook.createDataFormat().getFormat("₹#,##0.00"));

            String[] columns = {
                    "Sr. No.", "Product Name", "Current Stock", "Total Stock In",
                    "Total Stock Out", "Available Stock", "Inventory Value (₹)", "Status"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            int srNo = 1;
            for (Product product : allProducts) {
                ProductReportResponse report = buildReport(product, dateFrom, dateTo);
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(srNo++);
                row.createCell(1).setCellValue(report.getProductName());
                row.createCell(2).setCellValue(report.getCurrentStock());
                row.createCell(3).setCellValue(report.getTotalStockIn());
                row.createCell(4).setCellValue(report.getTotalStockOut());
                row.createCell(5).setCellValue(report.getAvailableStock());

                Cell valueCell = row.createCell(6);
                valueCell.setCellValue(report.getInventoryValue() != null
                        ? report.getInventoryValue().doubleValue() : 0.0);
                valueCell.setCellStyle(currencyStyle);

                row.createCell(7).setCellValue(report.getStatus());
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to export reports to Excel", e);
        }
    }

    private List<Product> getAllProducts(String keyword, String status) {
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        boolean hasStatus = status != null && !status.isBlank();

        if (hasKeyword && hasStatus) {
            return productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStatus(
                    keyword, keyword, ProductStatus.valueOf(status.toUpperCase()), Pageable.unpaged()).getContent();
        } else if (hasStatus) {
            return productRepository.findByStatus(ProductStatus.valueOf(status.toUpperCase()), Pageable.unpaged()).getContent();
        } else if (hasKeyword) {
            return productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                    keyword, keyword, Pageable.unpaged()).getContent();
        } else {
            return productRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        }
    }
}
