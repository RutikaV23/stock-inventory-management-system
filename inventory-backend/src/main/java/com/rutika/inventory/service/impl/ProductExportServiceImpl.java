package com.rutika.inventory.service.impl;

import com.rutika.inventory.entity.Product;
import com.rutika.inventory.repository.ProductRepository;
import com.rutika.inventory.service.interfaces.ProductExportService;
import com.rutika.inventory.util.ExcelUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductExportServiceImpl implements ProductExportService {

    private final ProductRepository productRepository;
    private final ExcelUtility excelUtility;

    @Override
    @Transactional(readOnly = true)
    public byte[] exportProductsToExcel() {
        List<Product> products = productRepository.findAll();
        return excelUtility.exportProducts(products);
    }
}
