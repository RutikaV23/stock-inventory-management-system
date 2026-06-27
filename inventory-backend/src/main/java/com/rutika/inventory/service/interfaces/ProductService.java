package com.rutika.inventory.service.interfaces;

import com.rutika.inventory.dto.request.ProductRequest;
import com.rutika.inventory.dto.response.ProductResponse;
import com.rutika.inventory.response.PageResponse;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    ProductResponse getProductById(String id);

    PageResponse<ProductResponse> getAllProducts(int page, int size, String sort, String keyword);

    ProductResponse updateProduct(String id, ProductRequest request);

    void deleteProduct(String id);
}
