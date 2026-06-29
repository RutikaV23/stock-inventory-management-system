package com.rutika.inventory;

import com.rutika.inventory.dto.request.ChangePasswordRequest;
import com.rutika.inventory.dto.request.LoginRequest;
import com.rutika.inventory.dto.request.RefreshTokenRequest;
import com.rutika.inventory.dto.request.UpdateProfileRequest;
import com.rutika.inventory.entity.RefreshToken;
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

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthIntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    private Role defaultRole;

    private RestClient rest;

    @BeforeEach
    void setUp() {
        refreshTokenRepository.deleteAll();
        userRepository.findByEmail("password-test@example.com")
                .ifPresent(user -> {
                    refreshTokenRepository.deleteByUserId(user.getId());
                    userRepository.delete(user);
                });

        defaultRole = roleRepository.findByRoleName("SUPER_ADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setRoleName("SUPER_ADMIN");
                    return roleRepository.save(role);
                });

        rest = RestClient.create("http://localhost:" + port);
    }

    private record AuthLoginResponse(boolean success, String message, AuthData data, String timestamp) {}
    private record AuthData(String accessToken, String refreshToken, long expiresIn, Object user) {}
    private record ApiResponseHelper(boolean success, String message, Object data, String timestamp) {}

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractData(Object data) {
        return (Map<String, Object>) data;
    }

    @Test
    void login_shouldReturn200AndTokens() {
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
        assertThat(response.getBody().success()).isTrue();
        assertThat(response.getBody().data().accessToken()).isNotBlank();
        assertThat(response.getBody().data().refreshToken()).isNotBlank();
        assertThat(response.getBody().data().expiresIn()).isGreaterThan(0);
        assertThat(response.getBody().data().user()).isNotNull();
    }

    @Test
    void login_withInvalidPassword_shouldReturn400() {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@gmail.com");
        request.setPassword("WrongPassword123");

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
    void login_withNonExistentEmail_shouldReturn400() {
        LoginRequest request = new LoginRequest();
        request.setEmail("nonexistent@example.com");
        request.setPassword("SomePass123");

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
    void login_withInactiveUser_shouldReturn400() {
        User inactiveUser = new User();
        inactiveUser.setFirstName("Inactive");
        inactiveUser.setLastName("User");
        inactiveUser.setEmail("inactive@example.com");
        inactiveUser.setPassword(passwordEncoder.encode("TestPass123"));
        inactiveUser.setStatus(UserStatus.INACTIVE);
        inactiveUser.setRole(defaultRole);
        userRepository.save(inactiveUser);

        LoginRequest request = new LoginRequest();
        request.setEmail("inactive@example.com");
        request.setPassword("TestPass123");

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

        userRepository.delete(inactiveUser);
    }

    @Test
    void accessProtectedEndpoint_withValidToken_shouldReturn200() {
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("admin@gmail.com");
        loginReq.setPassword("Admin@123");
        var loginResp = rest.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(loginReq)
                .retrieve()
                .toEntity(AuthLoginResponse.class);
        String token = loginResp.getBody().data().accessToken();

        var response = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + token)
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
    void accessProtectedEndpoint_withNoToken_shouldReturn401() {
        var response = rest.get()
                .uri("/api/v1/products?page=0&size=1")
                .retrieve()
                .onStatus(HttpStatus.UNAUTHORIZED::equals, (req, res) -> {})
                .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void accessProtectedEndpoint_withInvalidToken_shouldReturn401() {
        var response = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer invalidTokenHere")
                .build()
                .get()
                .uri("/api/v1/products?page=0&size=1")
                .retrieve()
                .onStatus(HttpStatus.UNAUTHORIZED::equals, (req, res) -> {})
                .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void refreshToken_withValidToken_shouldReturn200() {
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("admin@gmail.com");
        loginReq.setPassword("Admin@123");
        var loginResp = rest.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(loginReq)
                .retrieve()
                .toEntity(AuthLoginResponse.class);
        String refreshToken = loginResp.getBody().data().refreshToken();

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(refreshToken);

        var response = rest.post()
                .uri("/api/v1/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(AuthLoginResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();
        assertThat(response.getBody().data().accessToken()).isNotBlank();
    }

    @Test
    void refreshToken_withInvalidToken_shouldReturn400() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("invalid-refresh-token");

        var response = rest.post()
                .uri("/api/v1/auth/refresh-token")
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
    void refreshToken_withRevokedToken_shouldReturn400() {
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("admin@gmail.com");
        loginReq.setPassword("Admin@123");
        var loginResp = rest.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(loginReq)
                .retrieve()
                .toEntity(AuthLoginResponse.class);
        String accessToken = loginResp.getBody().data().accessToken();
        String refreshToken = loginResp.getBody().data().refreshToken();
        var authRest = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + accessToken)
                .build();

        RefreshTokenRequest logoutReq = new RefreshTokenRequest();
        logoutReq.setRefreshToken(refreshToken);
        authRest.post()
                .uri("/api/v1/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .body(logoutReq)
                .retrieve()
                .toBodilessEntity();

        RefreshTokenRequest refreshReq = new RefreshTokenRequest();
        refreshReq.setRefreshToken(refreshToken);
        var response = rest.post()
                .uri("/api/v1/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .body(refreshReq)
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals, (req, res) -> {})
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isFalse();
    }

    @Test
    void refreshToken_withExpiredToken_shouldReturn400() {
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("admin@gmail.com");
        loginReq.setPassword("Admin@123");
        var loginResp = rest.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(loginReq)
                .retrieve()
                .toEntity(AuthLoginResponse.class);
        String refreshTokenStr = loginResp.getBody().data().refreshToken();

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshTokenStr).orElseThrow();
        storedToken.setExpiresAt(Instant.now().minusSeconds(3600));
        refreshTokenRepository.save(storedToken);

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(refreshTokenStr);

        var response = rest.post()
                .uri("/api/v1/auth/refresh-token")
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
    void logout_shouldInvalidateRefreshToken() {
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("admin@gmail.com");
        loginReq.setPassword("Admin@123");
        var loginResp = rest.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(loginReq)
                .retrieve()
                .toEntity(AuthLoginResponse.class);
        String accessToken = loginResp.getBody().data().accessToken();
        String refreshToken = loginResp.getBody().data().refreshToken();
        var authRest = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + accessToken)
                .build();

        RefreshTokenRequest logoutReq = new RefreshTokenRequest();
        logoutReq.setRefreshToken(refreshToken);
        var logoutResponse = authRest.post()
                .uri("/api/v1/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .body(logoutReq)
                .retrieve()
                .toBodilessEntity();

        assertThat(logoutResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        RefreshTokenRequest refreshReq = new RefreshTokenRequest();
        refreshReq.setRefreshToken(refreshToken);
        var refreshResponse = rest.post()
                .uri("/api/v1/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .body(refreshReq)
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals, (req, res) -> {})
                .toEntity(ApiResponseHelper.class);

        assertThat(refreshResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getProfile_shouldReturn200() {
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("admin@gmail.com");
        loginReq.setPassword("Admin@123");
        var loginResp = rest.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(loginReq)
                .retrieve()
                .toEntity(AuthLoginResponse.class);
        String token = loginResp.getBody().data().accessToken();

        var response = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + token)
                .build()
                .get()
                .uri("/api/v1/auth/profile")
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        Map<String, Object> data = extractData(response.getBody().data());
        assertThat(data.get("email")).isEqualTo("admin@gmail.com");
        assertThat(data.get("firstName")).isEqualTo("Rutika");
    }

    @Test
    void updateProfile_shouldReturn200AndUpdatedData() {
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("admin@gmail.com");
        loginReq.setPassword("Admin@123");
        var loginResp = rest.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(loginReq)
                .retrieve()
                .toEntity(AuthLoginResponse.class);
        String token = loginResp.getBody().data().accessToken();

        UpdateProfileRequest updateReq = new UpdateProfileRequest();
        updateReq.setFirstName("Updated");
        updateReq.setLastName("Name");
        updateReq.setPhone("+9876543210");

        var response = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + token)
                .build()
                .put()
                .uri("/api/v1/auth/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateReq)
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        Map<String, Object> data = extractData(response.getBody().data());
        assertThat(data.get("firstName")).isEqualTo("Updated");
        assertThat(data.get("lastName")).isEqualTo("Name");
        assertThat(data.get("phone")).isEqualTo("+9876543210");

        UpdateProfileRequest restoreReq = new UpdateProfileRequest();
        restoreReq.setFirstName("Rutika");
        restoreReq.setLastName("Admin");
        restoreReq.setPhone(null);
        RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + token)
                .build()
                .put()
                .uri("/api/v1/auth/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .body(restoreReq)
                .retrieve()
                .toEntity(ApiResponseHelper.class);
    }

    @Test
    void changePassword_withCorrectCurrentPassword_shouldReturn200() {
        User testUser = new User();
        testUser.setFirstName("Pass");
        testUser.setLastName("Test");
        testUser.setEmail("password-test@example.com");
        testUser.setPassword(passwordEncoder.encode("OldPass123"));
        testUser.setStatus(UserStatus.ACTIVE);
        testUser.setRole(defaultRole);
        userRepository.save(testUser);

        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("password-test@example.com");
        loginReq.setPassword("OldPass123");
        var loginResp = rest.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(loginReq)
                .retrieve()
                .toEntity(AuthLoginResponse.class);
        String token = loginResp.getBody().data().accessToken();

        ChangePasswordRequest changeReq = new ChangePasswordRequest();
        changeReq.setCurrentPassword("OldPass123");
        changeReq.setNewPassword("NewPass456");

        var response = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + token)
                .build()
                .put()
                .uri("/api/v1/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .body(changeReq)
                .retrieve()
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        LoginRequest newLoginReq = new LoginRequest();
        newLoginReq.setEmail("password-test@example.com");
        newLoginReq.setPassword("NewPass456");
        var newLoginResp = rest.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(newLoginReq)
                .retrieve()
                .toEntity(AuthLoginResponse.class);

        assertThat(newLoginResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(newLoginResp.getBody()).isNotNull();
        assertThat(newLoginResp.getBody().success()).isTrue();

        refreshTokenRepository.deleteByUserId(testUser.getId());
        userRepository.delete(testUser);
    }

    @Test
    void changePassword_withWrongCurrentPassword_shouldReturn400() {
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("admin@gmail.com");
        loginReq.setPassword("Admin@123");
        var loginResp = rest.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(loginReq)
                .retrieve()
                .toEntity(AuthLoginResponse.class);
        String token = loginResp.getBody().data().accessToken();

        ChangePasswordRequest changeReq = new ChangePasswordRequest();
        changeReq.setCurrentPassword("WrongCurrentPassword");
        changeReq.setNewPassword("NewPass456");

        var response = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("Authorization", "Bearer " + token)
                .build()
                .put()
                .uri("/api/v1/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .body(changeReq)
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals, (req, res) -> {})
                .toEntity(ApiResponseHelper.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isFalse();
    }
}
