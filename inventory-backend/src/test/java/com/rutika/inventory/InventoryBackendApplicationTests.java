package com.rutika.inventory;

import com.rutika.inventory.dto.request.ProductRequest;
import com.rutika.inventory.dto.request.StockInRequest;
import com.rutika.inventory.dto.request.StockOutRequest;
import com.rutika.inventory.entity.Product;
import com.rutika.inventory.entity.StockIn;
import com.rutika.inventory.enums.ProductStatus;
import com.rutika.inventory.enums.StockStatus;
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
import java.time.Instant;
import java.util.List;
import java.util.Map;

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

        Product laptop = new Product();
        laptop.setName("Laptop");
        laptop.setSku("LAP-001");
        laptop.setPrice(new BigDecimal("999.99"));
        laptop.setStockQuantity(10);
        laptop.setStatus(ProductStatus.ACTIVE);
        productRepository.save(laptop);
        productId = laptop.getId();

        Product mouse = new Product();
        mouse.setName("Mouse");
        mouse.setSku("MOU-001");
        mouse.setPrice(new BigDecimal("29.99"));
        mouse.setStockQuantity(50);
        mouse.setStatus(ProductStatus.ACTIVE);
        productRepository.save(mouse);

        Product keyboard = new Product();
        keyboard.setName("Keyboard");
        keyboard.setSku("KEY-001");
        keyboard.setPrice(new BigDecimal("79.99"));
        keyboard.setStockQuantity(30);
        keyboard.setStatus(ProductStatus.ACTIVE);
        productRepository.save(keyboard);

        Product monitor = new Product();
        monitor.setName("Monitor");
        monitor.setSku("MON-001");
        monitor.setDescription("27 inch 4K display");
        monitor.setPrice(new BigDecimal("499.99"));
        monitor.setStockQuantity(15);
        monitor.setStatus(ProductStatus.ACTIVE);
        productRepository.save(monitor);

        StockIn stockIn1 = new StockIn();
        stockIn1.setProduct(laptop);
        stockIn1.setQuantity(5);
        stockIn1.setReferenceNumber("PO-001");
        stockIn1.setNotes("Initial laptop stock");
        stockIn1.setStatus(StockStatus.ACTIVE);
        stockIn1.setCreatedAt(Instant.parse("2026-06-01T10:00:00Z"));
        stockInRepository.save(stockIn1);
        laptop.setStockQuantity(15);
        productRepository.save(laptop);

        StockIn stockIn2 = new StockIn();
        stockIn2.setProduct(mouse);
        stockIn2.setQuantity(20);
        stockIn2.setReferenceNumber("PO-002");
        stockIn2.setNotes("Mouse restock");
        stockIn2.setStatus(StockStatus.ACTIVE);
        stockIn2.setCreatedAt(Instant.parse("2026-06-02T10:00:00Z"));
        stockInRepository.save(stockIn2);
        mouse.setStockQuantity(70);
        productRepository.save(mouse);

        StockIn stockIn3 = new StockIn();
        stockIn3.setProduct(keyboard);
        stockIn3.setQuantity(10);
        stockIn3.setReferenceNumber("PO-003");
        stockIn3.setNotes("Keyboard restock");
        stockIn3.setStatus(StockStatus.ACTIVE);
        stockIn3.setCreatedAt(Instant.parse("2026-06-03T10:00:00Z"));
        stockInRepository.save(stockIn3);
        keyboard.setStockQuantity(40);
        productRepository.save(keyboard);

        StockIn stockIn4 = new StockIn();
        stockIn4.setProduct(monitor);
        stockIn4.setQuantity(8);
        stockIn4.setReferenceNumber("PO-004");
        stockIn4.setNotes("Monitor restock for new display models");
        stockIn4.setStatus(StockStatus.ACTIVE);
        stockIn4.setCreatedAt(Instant.parse("2026-06-04T10:00:00Z"));
        stockInRepository.save(stockIn4);
        monitor.setStockQuantity(23);
        productRepository.save(monitor);

        rest = RestClient.create("http://localhost:" + port);
    }

    @Test
    void createProduct_shouldReturn201() {
        ProductRequest request = new ProductRequest();
        request.setName("Tablet");
        request.setSku("TAB-001");
        request.setPrice(new BigDecimal("199.99"));

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

    @Test
    void getAllProducts_withKeyword_shouldReturnMatchingResults() {
        var response = rest.get()
                .uri("/api/v1/products?keyword=lap")
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        Map<String, Object> data = extractData(response.getBody().data());
        assertThat(data.get("totalElements")).isEqualTo(1);
    }

    @Test
    void getAllProducts_withKeywordNoMatch_shouldReturnEmptyResults() {
        var response = rest.get()
                .uri("/api/v1/products?keyword=xyzzy")
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        Map<String, Object> data = extractData(response.getBody().data());
        assertThat(data.get("totalElements")).isEqualTo(0);
    }

    @Test
    void getAllProducts_withSortByNameAsc_shouldReturnOrderedResults() {
        var response = rest.get()
                .uri("/api/v1/products?sort=name,asc&size=10")
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        Map<String, Object> data = extractData(response.getBody().data());
        assertThat(data.get("totalElements")).isEqualTo(4);

        java.util.List<Map<String, Object>> content = (java.util.List<Map<String, Object>>) data.get("content");
        assertThat(content.get(0).get("name")).isEqualTo("Keyboard");
        assertThat(content.get(1).get("name")).isEqualTo("Laptop");
        assertThat(content.get(2).get("name")).isEqualTo("Monitor");
        assertThat(content.get(3).get("name")).isEqualTo("Mouse");
    }

    @Test
    void getAllProducts_withSortByNameDesc_shouldReturnOrderedResults() {
        var response = rest.get()
                .uri("/api/v1/products?sort=name,desc&size=10")
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        Map<String, Object> data = extractData(response.getBody().data());
        assertThat(data.get("totalElements")).isEqualTo(4);

        java.util.List<Map<String, Object>> content = (java.util.List<Map<String, Object>>) data.get("content");
        assertThat(content.get(0).get("name")).isEqualTo("Mouse");
        assertThat(content.get(3).get("name")).isEqualTo("Keyboard");
    }

    @Test
    void getAllProducts_withKeywordAndSortAndPagination_shouldReturnCombinedResults() {
        var response = rest.get()
                .uri("/api/v1/products?page=0&size=10&sort=price,desc&keyword=mo")
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        Map<String, Object> data = extractData(response.getBody().data());
        assertThat(data.get("totalElements")).isEqualTo(2);

        java.util.List<Map<String, Object>> content = (java.util.List<Map<String, Object>>) data.get("content");
        assertThat(content.get(0).get("name")).isEqualTo("Monitor");
        assertThat(content.get(1).get("name")).isEqualTo("Mouse");
    }

    @Test
    void getAllProducts_withPaginationPage1_shouldReturnSecondPage() {
        var response = rest.get()
                .uri("/api/v1/products?page=0&size=2")
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        Map<String, Object> data = extractData(response.getBody().data());
        assertThat(data.get("totalElements")).isEqualTo(4);
        assertThat(data.get("page")).isEqualTo(0);
        assertThat(data.get("size")).isEqualTo(2);
        assertThat(data.get("totalPages")).isEqualTo(2);

        var page1 = rest.get()
                .uri("/api/v1/products?page=1&size=2")
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        Map<String, Object> page1Data = extractData(page1.getBody().data());
        assertThat(page1Data.get("page")).isEqualTo(1);
        assertThat(page1Data.get("first")).isEqualTo(false);
        assertThat(page1Data.get("last")).isEqualTo(true);
    }

    @Test
    void getStockInHistory_shouldReturnPage() {
        var response = rest.get()
                .uri("/api/v1/stock/in/history?page=0&size=10")
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        Map<String, Object> data = extractData(response.getBody().data());
        assertThat(data.get("totalElements")).isEqualTo(4);
        assertThat(data.get("page")).isEqualTo(0);
        assertThat(data.get("totalPages")).isEqualTo(1);
    }

    @Test
    void getStockInHistory_withPagination_shouldReturnCorrectPage() {
        var response = rest.get()
                .uri("/api/v1/stock/in/history?page=0&size=2")
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        Map<String, Object> data = extractData(response.getBody().data());
        assertThat(data.get("totalElements")).isEqualTo(4);
        assertThat(data.get("page")).isEqualTo(0);
        assertThat(data.get("size")).isEqualTo(2);
        assertThat(data.get("totalPages")).isEqualTo(2);
        assertThat(data.get("first")).isEqualTo(true);

        var page1 = rest.get()
                .uri("/api/v1/stock/in/history?page=1&size=2")
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        Map<String, Object> page1Data = extractData(page1.getBody().data());
        assertThat(page1Data.get("page")).isEqualTo(1);
        assertThat(page1Data.get("first")).isEqualTo(false);
        assertThat(page1Data.get("last")).isEqualTo(true);
    }

    @Test
    void getStockInHistory_withSortByQuantityAsc_shouldReturnOrderedResults() {
        var response = rest.get()
                .uri("/api/v1/stock/in/history?sort=quantity,asc&size=10")
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        Map<String, Object> data = extractData(response.getBody().data());
        assertThat(data.get("totalElements")).isEqualTo(4);

        List<Map<String, Object>> content = (List<Map<String, Object>>) data.get("content");
        assertThat((Integer) content.get(0).get("quantity")).isEqualTo(5);
        assertThat((Integer) content.get(1).get("quantity")).isEqualTo(8);
        assertThat((Integer) content.get(2).get("quantity")).isEqualTo(10);
        assertThat((Integer) content.get(3).get("quantity")).isEqualTo(20);
    }

    @Test
    void getStockInHistory_withSortByQuantityDesc_shouldReturnOrderedResults() {
        var response = rest.get()
                .uri("/api/v1/stock/in/history?sort=quantity,desc&size=10")
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        Map<String, Object> data = extractData(response.getBody().data());
        assertThat(data.get("totalElements")).isEqualTo(4);

        List<Map<String, Object>> content = (List<Map<String, Object>>) data.get("content");
        assertThat((Integer) content.get(0).get("quantity")).isEqualTo(20);
        assertThat((Integer) content.get(3).get("quantity")).isEqualTo(5);
    }

    @Test
    void getStockInHistory_withKeyword_shouldReturnMatchingResults() {
        var response = rest.get()
                .uri("/api/v1/stock/in/history?keyword=lap")
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        Map<String, Object> data = extractData(response.getBody().data());
        assertThat(data.get("totalElements")).isEqualTo(1);
    }

    @Test
    void getStockInHistory_withKeywordNoMatch_shouldReturnEmptyResults() {
        var response = rest.get()
                .uri("/api/v1/stock/in/history?keyword=xyzzy")
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        Map<String, Object> data = extractData(response.getBody().data());
        assertThat(data.get("totalElements")).isEqualTo(0);
    }

    @Test
    void getStockInHistory_withKeywordAndSortAndPagination_shouldReturnCombinedResults() {
        var response = rest.get()
                .uri("/api/v1/stock/in/history?page=0&size=10&sort=quantity,desc&keyword=restock")
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        Map<String, Object> data = extractData(response.getBody().data());
        assertThat(data.get("totalElements")).isEqualTo(3);

        List<Map<String, Object>> content = (List<Map<String, Object>>) data.get("content");
        assertThat((Integer) content.get(0).get("quantity")).isEqualTo(20);
    }

    @Test
    void getStockInHistory_withInvalidSortDirection_shouldReturn400() {
        var response = rest.get()
                .uri("/api/v1/stock/in/history?sort=quantity,invalid")
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals, (req, res) -> {})
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isFalse();
    }

    @Test
    void getStockInHistory_defaultSort_shouldReturnNewestFirst() {
        var response = rest.get()
                .uri("/api/v1/stock/in/history?size=10")
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        Map<String, Object> data = extractData(response.getBody().data());
        assertThat(data.get("totalElements")).isEqualTo(4);

        List<Map<String, Object>> content = (List<Map<String, Object>>) data.get("content");
        String firstItemDate = (String) content.get(0).get("stockInDate");
        String lastItemDate = (String) content.get(3).get("stockInDate");
        assertThat(firstItemDate).isGreaterThan(lastItemDate);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractData(Object data) {
        return (Map<String, Object>) data;
    }

    private record ApiResponseHelper(boolean success, String message, Object data, String timestamp) {}
}