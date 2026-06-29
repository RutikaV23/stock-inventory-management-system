package com.rutika.inventory.util;

import com.rutika.inventory.entity.Product;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ExcelUtility {

    public byte[] exportProducts(List<Product> products) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Products");

            String[] columns = {
                "Product Name", "Description", "Price",
                "Stock Quantity", "Minimum Stock", "Status", "Created At"
            };

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setFont(createHeaderFont(workbook));

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (Product product : products) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(product.getName());
                row.createCell(1).setCellValue(product.getDescription());
                row.createCell(2).setCellValue(product.getPrice() != null ? product.getPrice().doubleValue() : 0);
                row.createCell(3).setCellValue(product.getStockQuantity());
                row.createCell(4).setCellValue(product.getMinimumStock() != null ? product.getMinimumStock() : 0);
                row.createCell(5).setCellValue(product.getStatus() != null ? product.getStatus().name() : "");
                row.createCell(6).setCellValue(product.getCreatedAt() != null ? product.getCreatedAt().toString() : "");
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to export Excel file", e);
        }
    }

    private Font createHeaderFont(XSSFWorkbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        return font;
    }
}
