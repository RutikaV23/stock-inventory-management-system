package com.rutika.inventory.controller;

import com.rutika.inventory.constants.ApiConstants;
import com.rutika.inventory.constants.MessageConstants;
import com.rutika.inventory.dto.request.ProductRequest;
import com.rutika.inventory.dto.response.ProductResponse;
import com.rutika.inventory.response.ApiResponse;
import com.rutika.inventory.response.PageResponse;
import com.rutika.inventory.service.interfaces.ProductExportService;
import com.rutika.inventory.service.interfaces.ProductService;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@Tag(name = "Products", description = "Product management endpoints for CRUD operations and Excel export")
public class ProductController {

    private final ProductService productService;
    private final ProductExportService productExportService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new product",
               description = "Creates a new product with the provided details.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "Product created successfully",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiResponse.class),
                        examples = @ExampleObject(value = """
                                {
                                    "success": true,
                                    "message": "Product created successfully",
                                    "data": {
                                        "id": "550e8400-e29b-41d4-a716-446655440000",
                                        "name": "Wireless Mouse",
                                        "description": "Ergonomic wireless mouse with USB receiver",
                                        "price": 29.99,
                                        "stockQuantity": 0,
                                        "status": "ACTIVE",
                                        "createdAt": "2026-06-27T10:30:00Z",
                                        "updatedAt": "2026-06-27T10:30:00Z"
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
                responseCode = "409",
                description = "A product with the given name already exists",
                content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                                {
                                    "success": false,
                                    "message": "Product already exists",
                                    "data": null,
                                    "timestamp": "2026-06-27T10:30:00Z"
                                }
                                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = @Content(mediaType = "application/json"))
    })
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ApiResponse.success(MessageConstants.PRODUCT + MessageConstants.CREATED_SUCCESS, response);
    }

    @GetMapping(ApiConstants.ID_PATH_VARIABLE)
    @Operation(summary = "Get product by ID",
               description = "Retrieves a single product by its unique identifier")
    @Parameter(name = "id", description = "Unique product identifier (UUID)", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Product retrieved successfully",
                content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                                {
                                    "success": true,
                                    "message": "Product retrieved successfully",
                                    "data": {
                                        "id": "550e8400-e29b-41d4-a716-446655440000",
                                        "name": "Wireless Mouse",
                                        "description": "Ergonomic wireless mouse with USB receiver",
                                        "price": 29.99,
                                        "stockQuantity": 150,
                                        "status": "ACTIVE",
                                        "createdAt": "2026-06-27T10:30:00Z",
                                        "updatedAt": "2026-06-27T10:30:00Z"
                                    },
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
        @Parameter(name = "keyword", description = "Search keyword (matches name or description)", example = "mouse")
    })
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Products retrieved successfully",
                content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                                {
                                    "success": true,
                                    "message": "Product retrieved successfully",
                                    "data": {
                                        "content": [
                                            {
                                                "id": "550e8400-e29b-41d4-a716-446655440000",
                                                "name": "Wireless Mouse",
                                                "description": "Ergonomic wireless mouse with USB receiver",
                                                "price": 29.99,
                                                "stockQuantity": 150,
                                                "status": "ACTIVE",
                                                "createdAt": "2026-06-27T10:30:00Z",
                                                "updatedAt": "2026-06-27T10:30:00Z"
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
    public ApiResponse<PageResponse<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = ApiConstants.PAGE_DEFAULT) int page,
            @RequestParam(defaultValue = ApiConstants.SIZE_DEFAULT) int size,
            @RequestParam(defaultValue = ApiConstants.SORT_DEFAULT) String sort,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        PageResponse<ProductResponse> response = productService.getAllProducts(page, size, sort, keyword, status);
        return ApiResponse.success(MessageConstants.PRODUCT + MessageConstants.RETRIEVED_SUCCESS, response);
    }

    @PutMapping(ApiConstants.ID_PATH_VARIABLE)
    @Operation(summary = "Update an existing product",
               description = "Updates the details of an existing product identified by its ID")
    @Parameter(name = "id", description = "Unique product identifier (UUID)", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Product updated successfully",
                content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                                {
                                    "success": true,
                                    "message": "Product updated successfully",
                                    "data": {
                                        "id": "550e8400-e29b-41d4-a716-446655440000",
                                        "name": "Wireless Mouse Pro",
                                        "description": "Updated ergonomic wireless mouse with USB receiver",
                                        "price": 39.99,
                                        "stockQuantity": 150,
                                        "status": "ACTIVE",
                                        "createdAt": "2026-06-27T10:30:00Z",
                                        "updatedAt": "2026-06-27T11:00:00Z"
                                    },
                                    "timestamp": "2026-06-27T11:00:00Z"
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
                description = "A product with the given name already exists",
                content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                                {
                                    "success": false,
                                    "message": "Product already exists",
                                    "data": null,
                                    "timestamp": "2026-06-27T10:30:00Z"
                                }
                                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = @Content(mediaType = "application/json"))
    })
    public ApiResponse<ProductResponse> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.updateProduct(id, request);
        return ApiResponse.success(MessageConstants.PRODUCT + MessageConstants.UPDATED_SUCCESS, response);
    }

    @DeleteMapping(ApiConstants.ID_PATH_VARIABLE)
    @Operation(summary = "Delete a product",
               description = "Deletes an existing product identified by its ID")
    @Parameter(name = "id", description = "Unique product identifier (UUID)", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Product deleted successfully",
                content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                                {
                                    "success": true,
                                    "message": "Product deleted successfully",
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
    public ApiResponse<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ApiResponse.success(MessageConstants.PRODUCT + MessageConstants.DELETED_SUCCESS);
    }

    @GetMapping("/export/excel")
    @Operation(summary = "Export products to Excel",
               description = "Exports all products as an Excel (.xlsx) file with columns: Product Name, Description, Price, Stock Quantity, Minimum Stock, Status, Created At")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Excel file downloaded successfully",
                content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<byte[]> exportProductsToExcel() {
        byte[] excelData = productExportService.exportProductsToExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelData);
    }
}
