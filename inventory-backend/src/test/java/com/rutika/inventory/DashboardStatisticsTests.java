package com.rutika.inventory;

import com.rutika.inventory.dto.request.LoginRequest;
import com.rutika.inventory.entity.Product;
import com.rutika.inventory.entity.StockIn;
import com.rutika.inventory.entity.StockOut;
import com.rutika.inventory.enums.ProductStatus;
import com.rutika.inventory.enums.StockStatus;
import com.rutika.inventory.repository.ProductRepository;
import com.rutika.inventory.repository.RefreshTokenRepository;
import com.rutika.inventory.repository.StockInRepository;
import com.rutika.inventory.repository.StockOutRepository;
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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DashboardStatisticsTests {

    @LocalServerPort
    private int port;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockInRepository stockInRepository;

    @Autowired
    private StockOutRepository stockOutRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private RestClient rest;
    private String authToken;

    @BeforeEach
    void setUp() {
        refreshTokenRepository.deleteAll();
        stockOutRepository.deleteAll();
        stockInRepository.deleteAll();
        productRepository.deleteAll();

        authToken = loginAndGetToken();

        Product activeNormal = new Product();
        activeNormal.setName("Laptop");
        activeNormal.setSku("LAP-001");
        activeNormal.setPrice(new BigDecimal("999.99"));
        activeNormal.setStockQuantity(20);
        activeNormal.setReorderLevel(5);
        activeNormal.setStatus(ProductStatus.ACTIVE);
        productRepository.save(activeNormal);

        Product lowStock = new Product();
        lowStock.setName("Mouse");
        lowStock.setSku("MOU-001");
        lowStock.setPrice(new BigDecimal("29.99"));
        lowStock.setStockQuantity(3);
        lowStock.setReorderLevel(5);
        lowStock.setStatus(ProductStatus.ACTIVE);
        productRepository.save(lowStock);

        Product outOfStock = new Product();
        outOfStock.setName("Keyboard");
        outOfStock.setSku("KEY-001");
        outOfStock.setPrice(new BigDecimal("79.99"));
        outOfStock.setStockQuantity(0);
        outOfStock.setReorderLevel(10);
        outOfStock.setStatus(ProductStatus.ACTIVE);
        productRepository.save(outOfStock);

        Product inactiveProduct = new Product();
        inactiveProduct.setName("Old Tablet");
        inactiveProduct.setSku("TAB-001");
        inactiveProduct.setPrice(new BigDecimal("199.99"));
        inactiveProduct.setStockQuantity(5);
        inactiveProduct.setReorderLevel(2);
        inactiveProduct.setStatus(ProductStatus.INACTIVE);
        productRepository.save(inactiveProduct);

        Product discontinuedProduct = new Product();
        discontinuedProduct.setName("Old Phone");
        discontinuedProduct.setSku("PHO-001");
        discontinuedProduct.setPrice(new BigDecimal("99.99"));
        discontinuedProduct.setStockQuantity(0);
        discontinuedProduct.setReorderLevel(0);
        discontinuedProduct.setStatus(ProductStatus.DISCONTINUED);
        productRepository.save(discontinuedProduct);

        StockIn stockIn1 = new StockIn();
        stockIn1.setProduct(activeNormal);
        stockIn1.setQuantity(10);
        stockIn1.setReferenceNumber("PO-001");
        stockIn1.setStatus(StockStatus.ACTIVE);
        stockIn1.setCreatedAt(Instant.parse("2026-06-01T10:00:00Z"));
        stockInRepository.save(stockIn1);

        StockIn stockIn2 = new StockIn();
        stockIn2.setProduct(lowStock);
        stockIn2.setQuantity(5);
        stockIn2.setReferenceNumber("PO-002");
        stockIn2.setStatus(StockStatus.ACTIVE);
        stockIn2.setCreatedAt(Instant.parse("2026-06-02T10:00:00Z"));
        stockInRepository.save(stockIn2);

        StockOut stockOut1 = new StockOut();
        stockOut1.setProduct(activeNormal);
        stockOut1.setQuantity(3);
        stockOut1.setReason("Customer order");
        stockOut1.setStatus(StockStatus.ACTIVE);
        stockOut1.setCreatedAt(Instant.parse("2026-06-05T10:00:00Z"));
        stockOutRepository.save(stockOut1);

        StockOut stockOut2 = new StockOut();
        stockOut2.setProduct(lowStock);
        stockOut2.setQuantity(1);
        stockOut2.setReason("Sale");
        stockOut2.setStatus(StockStatus.ACTIVE);
        stockOut2.setCreatedAt(Instant.parse("2026-06-06T10:00:00Z"));
        stockOutRepository.save(stockOut2);

        rest = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + authToken)
                .build();
    }

    private String loginAndGetToken() {
        LoginRequest login = new LoginRequest();
        login.setEmail("admin@gmail.com");
        login.setPassword("Admin@123");
        var loginClient = RestClient.create("http://localhost:" + port);
        var response = loginClient.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(login)
                .retrieve()
                .toEntity(AuthLoginResponse.class);
        return response.getBody().data().accessToken();
    }

    private record AuthLoginResponse(boolean success, String message, AuthData data, String timestamp) {}
    private record AuthData(String accessToken, String refreshToken, long expiresIn, Object user) {}

    private record ApiResponseHelper(boolean success, String message, Object data, String timestamp) {}

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractData(Object data) {
        return (Map<String, Object>) data;
    }

    @Test
    void getStatistics_shouldReturnAllCounts() {
        var response = rest.get()
                .uri("/api/v1/dashboard/statistics")
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        Map<String, Object> data = extractData(response.getBody().data());
        assertThat(data.get("totalProducts")).isEqualTo(5);
        assertThat(data.get("activeProducts")).isEqualTo(3);
        assertThat(data.get("inactiveProducts")).isEqualTo(1);
        assertThat(data.get("discontinuedProducts")).isEqualTo(1);
        assertThat(((Number) data.get("totalStockQuantity")).longValue()).isEqualTo(28L);
        assertThat(((Number) data.get("lowStockProducts")).longValue()).isEqualTo(3L);
        assertThat(((Number) data.get("outOfStockProducts")).longValue()).isEqualTo(2L);
        assertThat(data.get("totalStockInTransactions")).isEqualTo(2);
        assertThat(data.get("totalStockOutTransactions")).isEqualTo(2);
    }

    @Test
    void getStatistics_withEmptyDatabase_shouldReturnZeros() {
        stockOutRepository.deleteAll();
        stockInRepository.deleteAll();
        productRepository.deleteAll();

        var response = rest.get()
                .uri("/api/v1/dashboard/statistics")
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        Map<String, Object> data = extractData(response.getBody().data());
        assertThat(data.get("totalProducts")).isEqualTo(0);
        assertThat(data.get("activeProducts")).isEqualTo(0);
        assertThat(data.get("inactiveProducts")).isEqualTo(0);
        assertThat(data.get("discontinuedProducts")).isEqualTo(0);
        assertThat(data.get("totalStockQuantity")).isEqualTo(0);
        assertThat(data.get("lowStockProducts")).isEqualTo(0);
        assertThat(data.get("outOfStockProducts")).isEqualTo(0);
        assertThat(data.get("totalStockInTransactions")).isEqualTo(0);
        assertThat(data.get("totalStockOutTransactions")).isEqualTo(0);
    }
}
