package com.rutika.inventory;

import com.rutika.inventory.dto.request.CreateUserRequest;
import com.rutika.inventory.dto.request.LoginRequest;
import com.rutika.inventory.dto.request.UpdateUserRequest;
import com.rutika.inventory.entity.Role;
import com.rutika.inventory.entity.User;
import com.rutika.inventory.enums.UserStatus;
import com.rutika.inventory.repository.RefreshTokenRepository;
import com.rutika.inventory.repository.RoleRepository;
import com.rutika.inventory.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserManagementAndRbacTests {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private RestClient rest;
    private String superAdminToken;
    private String adminToken;

    private record AuthLoginResponse(boolean success, String message, AuthData data, String timestamp) {}
    private record AuthData(String accessToken, String refreshToken, long expiresIn, String role, Object user) {}
    private record ApiResponseHelper(boolean success, String message, Object data, String timestamp) {}

    @BeforeEach
    void setUp() {
        refreshTokenRepository.deleteAll();
        for (String email : new String[]{"admintest@example.com", "newuser@example.com"}) {
            userRepository.findByEmail(email).ifPresent(u -> {
                refreshTokenRepository.deleteByUserId(u.getId());
                userRepository.delete(u);
            });
        }
        userRepository.findByEmail("adminuser@example.com").ifPresent(u -> {
            refreshTokenRepository.deleteByUserId(u.getId());
            userRepository.delete(u);
        });

        Role adminRole = roleRepository.findByRoleName("ADMIN")
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setRoleName("ADMIN");
                    return roleRepository.save(r);
                });

        if (userRepository.findByEmail("adminuser@example.com").isEmpty()) {
            User adminUser = new User();
            adminUser.setFirstName("Admin");
            adminUser.setLastName("Tester");
            adminUser.setEmail("adminuser@example.com");
            adminUser.setPassword(passwordEncoder.encode("AdminPass123"));
            adminUser.setRole(adminRole);
            adminUser.setStatus(UserStatus.ACTIVE);
            userRepository.save(adminUser);
        }

        superAdminToken = loginAndGetToken("admin@gmail.com", "Admin@123");
        adminToken = loginAndGetToken("adminuser@example.com", "AdminPass123");

        rest = RestClient.create("http://localhost:" + port);
    }

    private String loginAndGetToken(String email, String password) {
        LoginRequest login = new LoginRequest();
        login.setEmail(email);
        login.setPassword(password);
        var loginClient = RestClient.create("http://localhost:" + port);
        var response = loginClient.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(login)
                .retrieve()
                .toEntity(AuthLoginResponse.class);
        return response.getBody().data().accessToken();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractData(Object data) {
        return (Map<String, Object>) data;
    }

    // --- User CRUD: SUPER_ADMIN allowed ---

    @Test
    void superAdmin_createUser_shouldReturn201() {
        CreateUserRequest request = new CreateUserRequest();
        request.setFirstName("Test");
        request.setLastName("User");
        request.setEmail("newuser@example.com");
        request.setPassword("TestPass123");
        request.setRoleName("ADMIN");

        var response = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + superAdminToken)
                .build()
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        Map<String, Object> data = extractData(response.getBody().data());
        assertThat(data.get("email")).isEqualTo("newuser@example.com");
        assertThat(data.get("role")).isEqualTo("ADMIN");

        userRepository.findByEmail("newuser@example.com").ifPresent(u -> {
            refreshTokenRepository.deleteByUserId(u.getId());
            userRepository.delete(u);
        });
    }

    @Test
    void superAdmin_createUser_duplicateEmail_shouldReturn400() {
        CreateUserRequest request = new CreateUserRequest();
        request.setFirstName("Dup");
        request.setLastName("User");
        request.setEmail("adminuser@example.com");
        request.setPassword("TestPass123");
        request.setRoleName("ADMIN");

        var response = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + superAdminToken)
                .build()
                .post()
                .uri("/api/v1/users")
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
    void superAdmin_createUser_invalidRole_shouldReturn400() {
        CreateUserRequest request = new CreateUserRequest();
        request.setFirstName("Bad");
        request.setLastName("Role");
        request.setEmail("badrole@example.com");
        request.setPassword("TestPass123");
        request.setRoleName("NONEXISTENT");

        var response = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + superAdminToken)
                .build()
                .post()
                .uri("/api/v1/users")
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
    void superAdmin_getAllUsers_shouldReturn200() {
        var response = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + superAdminToken)
                .build()
                .get()
                .uri("/api/v1/users?page=0&size=10")
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();
    }

    @Test
    void superAdmin_getAllUsers_withKeywordSearch_shouldReturnMatching() {
        var response = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + superAdminToken)
                .build()
                .get()
                .uri("/api/v1/users?keyword=adminuser")
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        Map<String, Object> data = extractData(response.getBody().data());
        assertThat(((Number) data.get("totalElements")).longValue()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void superAdmin_getUserById_shouldReturn200() {
        User testUser = userRepository.findByEmail("adminuser@example.com").orElseThrow();

        var response = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + superAdminToken)
                .build()
                .get()
                .uri("/api/v1/users/{id}", testUser.getId())
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        Map<String, Object> data = extractData(response.getBody().data());
        assertThat(data.get("email")).isEqualTo("adminuser@example.com");
    }

    @Test
    void superAdmin_getUserById_notFound_shouldReturn404() {
        var response = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + superAdminToken)
                .build()
                .get()
                .uri("/api/v1/users/{id}", "nonexistent-id")
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, (req, res) -> {})
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isFalse();
    }

    @Test
    void superAdmin_updateUser_shouldReturn200() {
        User testUser = userRepository.findByEmail("adminuser@example.com").orElseThrow();

        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("Updated");
        request.setLastName("Name");
        request.setPhone("+1111111111");

        var response = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + superAdminToken)
                .build()
                .put()
                .uri("/api/v1/users/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        Map<String, Object> data = extractData(response.getBody().data());
        assertThat(data.get("firstName")).isEqualTo("Updated");
        assertThat(data.get("lastName")).isEqualTo("Name");
        assertThat(data.get("phone")).isEqualTo("+1111111111");

        testUser.setFirstName("Admin");
        testUser.setLastName("Tester");
        testUser.setPhone(null);
        userRepository.save(testUser);
    }

    @Test
    void superAdmin_updateUser_roleAndStatus_shouldReturn200() {
        User testUser = userRepository.findByEmail("adminuser@example.com").orElseThrow();

        UpdateUserRequest request = new UpdateUserRequest();
        request.setStatus("INACTIVE");

        var response = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + superAdminToken)
                .build()
                .put()
                .uri("/api/v1/users/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        Map<String, Object> data = extractData(response.getBody().data());
        assertThat(data.get("status")).isEqualTo("INACTIVE");

        testUser.setStatus(UserStatus.ACTIVE);
        userRepository.save(testUser);
    }

    @Test
    void superAdmin_deleteUser_shouldSoftDelete() {
        User testUser = userRepository.findByEmail("adminuser@example.com").orElseThrow();

        var response = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + superAdminToken)
                .build()
                .delete()
                .uri("/api/v1/users/{id}", testUser.getId())
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        User deleted = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(deleted.getStatus()).isEqualTo(UserStatus.INACTIVE);

        testUser.setStatus(UserStatus.ACTIVE);
        userRepository.save(testUser);
    }

    @Test
    void superAdmin_deleteUser_notFound_shouldReturn404() {
        var response = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + superAdminToken)
                .build()
                .delete()
                .uri("/api/v1/users/{id}", "nonexistent-id")
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, (req, res) -> {})
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isFalse();
    }

    // --- RBAC: ADMIN forbidden on user endpoints ---

    @Test
    void admin_createUser_shouldReturn403() {
        CreateUserRequest request = new CreateUserRequest();
        request.setFirstName("Hacker");
        request.setLastName("User");
        request.setEmail("hacker@example.com");
        request.setPassword("HackPass123");
        request.setRoleName("ADMIN");

        var response = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + adminToken)
                .build()
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatus.FORBIDDEN::equals, (req, res) -> {})
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void admin_getAllUsers_shouldReturn403() {
        var response = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + adminToken)
                .build()
                .get()
                .uri("/api/v1/users")
                .retrieve()
                .onStatus(HttpStatus.FORBIDDEN::equals, (req, res) -> {})
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void admin_getUserById_shouldReturn403() {
        var response = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + adminToken)
                .build()
                .get()
                .uri("/api/v1/users/{id}", "some-id")
                .retrieve()
                .onStatus(HttpStatus.FORBIDDEN::equals, (req, res) -> {})
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void admin_updateUser_shouldReturn403() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("Hacker");

        var response = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + adminToken)
                .build()
                .put()
                .uri("/api/v1/users/{id}", "some-id")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatus.FORBIDDEN::equals, (req, res) -> {})
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void admin_deleteUser_shouldReturn403() {
        var response = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + adminToken)
                .build()
                .delete()
                .uri("/api/v1/users/{id}", "some-id")
                .retrieve()
                .onStatus(HttpStatus.FORBIDDEN::equals, (req, res) -> {})
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    // --- RBAC: ADMIN allowed on product/stock/dashboard endpoints ---

    @Test
    void admin_getAllProducts_shouldReturn200() {
        var response = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + adminToken)
                .build()
                .get()
                .uri("/api/v1/products?page=0&size=1")
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();
    }

    @Test
    void admin_getDashboard_shouldReturn200() {
        var response = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + adminToken)
                .build()
                .get()
                .uri("/api/v1/dashboard/statistics")
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();
    }

    @Test
    void admin_getStockInHistory_shouldReturn200() {
        var response = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + adminToken)
                .build()
                .get()
                .uri("/api/v1/stock/in/history?page=0&size=1")
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();
    }

    // --- RBAC: SUPER_ADMIN allowed on all endpoints ---

    @Test
    void superAdmin_getAllProducts_shouldReturn200() {
        var response = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + superAdminToken)
                .build()
                .get()
                .uri("/api/v1/products?page=0&size=1")
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();
    }

    @Test
    void superAdmin_getDashboard_shouldReturn200() {
        var response = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + superAdminToken)
                .build()
                .get()
                .uri("/api/v1/dashboard/statistics")
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();
    }

    // --- Auth: login response contains role ---

    @Test
    void login_shouldReturnRole() {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@gmail.com");
        request.setPassword("Admin@123");

        var response = rest.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(AuthLoginResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().data().role()).isEqualTo("SUPER_ADMIN");
    }

    // --- Unauthenticated access ---

    @Test
    void accessUsersEndpoint_withoutToken_shouldReturn401() {
        var response = rest.get()
                .uri("/api/v1/users")
                .retrieve()
                .onStatus(HttpStatus.UNAUTHORIZED::equals, (req, res) -> {})
                .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void accessUsersEndpoint_withInvalidToken_shouldReturn401() {
        var response = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer invalid.jwt.token")
                .build()
                .get()
                .uri("/api/v1/users")
                .retrieve()
                .onStatus(HttpStatus.UNAUTHORIZED::equals, (req, res) -> {})
                .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    // --- Auth: empty fields ---

    @Test
    void login_withEmptyEmail_shouldReturn400() {
        LoginRequest request = new LoginRequest();
        request.setEmail("");
        request.setPassword("Admin@123");

        var response = rest.post()
                .uri("/api/v1/auth/login")
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
    void login_withEmptyPassword_shouldReturn400() {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@gmail.com");
        request.setPassword("");

        var response = rest.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals, (req, res) -> {})
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isFalse();
    }
}
