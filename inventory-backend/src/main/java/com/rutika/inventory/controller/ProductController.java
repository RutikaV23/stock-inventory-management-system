package com.rutika.inventory.controller;

import com.rutika.inventory.constants.ApiConstants;
import com.rutika.inventory.constants.MessageConstants;
import com.rutika.inventory.dto.request.ProductRequest;
import com.rutika.inventory.dto.response.ProductResponse;
import com.rutika.inventory.response.ApiResponse;
import com.rutika.inventory.response.PageResponse;
import com.rutika.inventory.service.interfaces.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstants.PRODUCT_PATH)
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ApiResponse.success(MessageConstants.PRODUCT + MessageConstants.CREATED_SUCCESS, response);
    }

    @GetMapping(ApiConstants.ID_PATH_VARIABLE)
    public ApiResponse<ProductResponse> getProductById(@PathVariable String id) {
        ProductResponse response = productService.getProductById(id);
        return ApiResponse.success(MessageConstants.PRODUCT + MessageConstants.RETRIEVED_SUCCESS, response);
    }

    @GetMapping
    @Operation(summary = "Get all products with pagination, sorting, and search",
               description = "Retrieves a paginated list of products with optional keyword search and sorting")
    @Parameters({
        @Parameter(name = "page", description = "Page number (zero-based)", example = "0"),
        @Parameter(name = "size", description = "Number of items per page", example = "10"),
        @Parameter(name = "sort", description = "Sort field and direction (e.g., name,asc or price,desc)", example = "id,asc"),
        @Parameter(name = "keyword", description = "Search keyword (matches name, SKU, or description)", example = "laptop")
    })
    public ApiResponse<PageResponse<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = ApiConstants.PAGE_DEFAULT) int page,
            @RequestParam(defaultValue = ApiConstants.SIZE_DEFAULT) int size,
            @RequestParam(defaultValue = ApiConstants.SORT_DEFAULT) String sort,
            @RequestParam(required = false) String keyword) {
        PageResponse<ProductResponse> response = productService.getAllProducts(page, size, sort, keyword);
        return ApiResponse.success(MessageConstants.PRODUCT + MessageConstants.RETRIEVED_SUCCESS, response);
    }

    @PutMapping(ApiConstants.ID_PATH_VARIABLE)
    public ApiResponse<ProductResponse> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.updateProduct(id, request);
        return ApiResponse.success(MessageConstants.PRODUCT + MessageConstants.UPDATED_SUCCESS, response);
    }

    @DeleteMapping(ApiConstants.ID_PATH_VARIABLE)
    public ApiResponse<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ApiResponse.success(MessageConstants.PRODUCT + MessageConstants.DELETED_SUCCESS);
    }
}
