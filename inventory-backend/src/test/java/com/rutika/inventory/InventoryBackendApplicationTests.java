package com.rutika.inventory;

import com.rutika.inventory.dto.request.ProductRequest;
import com.rutika.inventory.dto.request.StockInRequest;
import com.rutika.inventory.dto.request.StockOutRequest;
import com.rutika.inventory.entity.Product;
import com.rutika.inventory.enums.ProductStatus;
import com.rutika.inventory.repository.StockInRepository;
import com.rutika.inventory.repository.StockOutRepository;
import com.rutika.inventory.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryBackendApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockInRepository stockInRepository;

    @Autowired
    private StockOutRepository stockOutRepository;

    private RestClient rest;
    private String productId;

    @BeforeEach
    void setUp() {
        stockOutRepository.deleteAll();
        stockInRepository.deleteAll();
        productRepository.deleteAll();

        Product product = new Product();
        product.setName("Laptop");
        product.setSku("LAP-001");
        product.setPrice(new BigDecimal("999.99"));
        product.setStockQuantity(10);
        product.setStatus(ProductStatus.ACTIVE);
        productRepository.save(product);
        productId = product.getId();

        rest = RestClient.create("http://localhost:" + port);
    }

    @Test
    void createProduct_shouldReturn201() {
        ProductRequest request = new ProductRequest();
        request.setName("Mouse");
        request.setSku("MOU-001");
        request.setPrice(new BigDecimal("29.99"));

        var response = rest.post()
                .uri("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();
    }

    @Test
    void createProduct_duplicateSku_shouldReturn400() {
        ProductRequest request = new ProductRequest();
        request.setName("Duplicate");
        request.setSku("LAP-001");
        request.setPrice(new BigDecimal("10.00"));

        var response = rest.post()
                .uri("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals, (req, res) -> {})
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isFalse();
    }

    @Test
    void getProductById_shouldReturn200() {
        var response = rest.get()
                .uri("/api/v1/products/{id}", productId)
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();
    }

    @Test
    void getProductById_notFound_shouldReturn404() {
        var response = rest.get()
                .uri("/api/v1/products/{id}", "nonexistent")
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, (req, res) -> {})
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isFalse();
    }

    @Test
    void getAllProducts_shouldReturnPage() {
        var response = rest.get()
                .uri("/api/v1/products?page=0&size=10")
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();
    }

    @Test
    void addStock_shouldIncreaseQuantity() {
        StockInRequest request = new StockInRequest();
        request.setProductId(productId);
        request.setQuantity(5);
        request.setReferenceNumber("PO-001");

        var response = rest.post()
                .uri("/api/v1/stock/in")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        Product updated = productRepository.findById(productId).orElseThrow();
        assertThat(updated.getStockQuantity()).isEqualTo(15);
    }

    @Test
    void removeStock_shouldDecreaseQuantity() {
        StockOutRequest request = new StockOutRequest();
        request.setProductId(productId);
        request.setQuantity(3);
        request.setReason("Sale");

        var response = rest.post()
                .uri("/api/v1/stock/out")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        Product updated = productRepository.findById(productId).orElseThrow();
        assertThat(updated.getStockQuantity()).isEqualTo(7);
    }

    @Test
    void removeStock_insufficientStock_shouldReturn400() {
        StockOutRequest request = new StockOutRequest();
        request.setProductId(productId);
        request.setQuantity(100);
        request.setReason("Bulk sale");

        var response = rest.post()
                .uri("/api/v1/stock/out")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals, (req, res) -> {})
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isFalse();
    }

    @Test
    void createProduct_missingName_shouldReturn400() {
        ProductRequest request = new ProductRequest();
        request.setSku("ERR-001");
        request.setPrice(new BigDecimal("10.00"));

        var response = rest.post()
                .uri("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals, (req, res) -> {})
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isFalse();
    }

    private record ApiResponseHelper(boolean success, String message, Object data, String timestamp) {}
}