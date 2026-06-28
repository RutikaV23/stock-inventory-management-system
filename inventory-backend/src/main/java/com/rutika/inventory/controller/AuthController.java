package com.rutika.inventory.controller;

import com.rutika.inventory.constants.ApiConstants;
import com.rutika.inventory.dto.request.ChangePasswordRequest;
import com.rutika.inventory.dto.request.LoginRequest;
import com.rutika.inventory.dto.request.RefreshTokenRequest;
import com.rutika.inventory.dto.request.UpdateProfileRequest;
import com.rutika.inventory.dto.response.LoginResponse;
import com.rutika.inventory.dto.response.RefreshTokenResponse;
import com.rutika.inventory.dto.response.UserProfileResponse;
import com.rutika.inventory.response.ApiResponse;
import com.rutika.inventory.service.interfaces.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstants.AUTH_PATH)
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and user profile management endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user",
               description = "Authenticates a user with email and password, returning JWT access and refresh tokens")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Authentication successful",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiResponse.class),
                        examples = @ExampleObject(value = """
                                {
                                    "success": true,
                                    "message": "Login successful",
                                    "data": {
                                        "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                                        "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
                                        "expiresIn": 900,
                                        "user": {
                                            "id": "550e8400-e29b-41d4-a716-446655440000",
                                            "firstName": "Rutika",
                                            "lastName": "Admin",
                                            "email": "admin@gmail.com",
                                            "phone": null,
                                            "status": "ACTIVE",
                                            "lastLoginAt": "2026-06-27T10:30:00Z",
                                            "createdAt": "2026-06-27T10:30:00Z"
                                        }
                                    },
                                    "timestamp": "2026-06-27T10:30:00Z"
                                }
                                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "Invalid email or password",
                content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                                {
                                    "success": false,
                                    "message": "Invalid email or password",
                                    "data": null,
                                    "timestamp": "2026-06-27T10:30:00Z"
                                }
                                """)))
    })
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success("Login successful", response);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh access token",
               description = "Generates a new access token using a valid refresh token")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Token refreshed successfully",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiResponse.class),
                        examples = @ExampleObject(value = """
                                {
                                    "success": true,
                                    "message": "Token refreshed successfully",
                                    "data": {
                                        "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                                        "expiresIn": 900
                                    },
                                    "timestamp": "2026-06-27T10:30:00Z"
                                }
                                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "Invalid, expired, or revoked refresh token",
                content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                                {
                                    "success": false,
                                    "message": "Invalid refresh token",
                                    "data": null,
                                    "timestamp": "2026-06-27T10:30:00Z"
                                }
                                """)))
    })
    public ApiResponse<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = authService.refreshToken(request);
        return ApiResponse.success("Token refreshed successfully", response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user",
               description = "Invalidates the refresh token, logging the user out")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Logged out successfully",
                content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                                {
                                    "success": true,
                                    "message": "Logged out successfully",
                                    "data": null,
                                    "timestamp": "2026-06-27T10:30:00Z"
                                }
                                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "Invalid refresh token",
                content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                                {
                                    "success": false,
                                    "message": "Invalid refresh token",
                                    "data": null,
                                    "timestamp": "2026-06-27T10:30:00Z"
                                }
                                """)))
    })
    public ApiResponse<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ApiResponse.success("Logged out successfully");
    }

    @GetMapping("/profile")
    @Operation(summary = "Get user profile",
               description = "Retrieves the profile of the currently authenticated user")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Profile retrieved successfully",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiResponse.class),
                        examples = @ExampleObject(value = """
                                {
                                    "success": true,
                                    "message": "Profile retrieved successfully",
                                    "data": {
                                        "id": "550e8400-e29b-41d4-a716-446655440000",
                                        "firstName": "Rutika",
                                        "lastName": "Admin",
                                        "email": "admin@gmail.com",
                                        "phone": null,
                                        "status": "ACTIVE",
                                        "lastLoginAt": "2026-06-27T10:30:00Z",
                                        "createdAt": "2026-06-27T10:30:00Z"
                                    },
                                    "timestamp": "2026-06-27T10:30:00Z"
                                }
                                """)))
    })
    public ApiResponse<UserProfileResponse> getProfile() {
        UserProfileResponse response = authService.getProfile();
        return ApiResponse.success("Profile retrieved successfully", response);
    }

    @PutMapping("/profile")
    @Operation(summary = "Update user profile",
               description = "Updates the first name, last name, and phone of the currently authenticated user")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Profile updated successfully",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiResponse.class),
                        examples = @ExampleObject(value = """
                                {
                                    "success": true,
                                    "message": "Profile updated successfully",
                                    "data": {
                                        "id": "550e8400-e29b-41d4-a716-446655440000",
                                        "firstName": "Rutika",
                                        "lastName": "Admin",
                                        "email": "admin@gmail.com",
                                        "phone": "+1234567890",
                                        "status": "ACTIVE",
                                        "lastLoginAt": "2026-06-27T10:30:00Z",
                                        "createdAt": "2026-06-27T10:30:00Z"
                                    },
                                    "timestamp": "2026-06-27T10:30:00Z"
                                }
                                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "Validation error",
                content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                                {
                                    "success": false,
                                    "message": "Validation failed",
                                    "data": null,
                                    "timestamp": "2026-06-27T10:30:00Z"
                                }
                                """)))
    })
    public ApiResponse<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        UserProfileResponse response = authService.updateProfile(request);
        return ApiResponse.success("Profile updated successfully", response);
    }

    @PutMapping("/change-password")
    @Operation(summary = "Change password",
               description = "Changes the password for the currently authenticated user. Requires the current password for verification.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Password changed successfully",
                content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                                {
                                    "success": true,
                                    "message": "Password changed successfully",
                                    "data": null,
                                    "timestamp": "2026-06-27T10:30:00Z"
                                }
                                """))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "Current password is incorrect",
                content = @Content(mediaType = "application/json",
                        examples = @ExampleObject(value = """
                                {
                                    "success": false,
                                    "message": "Current password is incorrect",
                                    "data": null,
                                    "timestamp": "2026-06-27T10:30:00Z"
                                }
                                """)))
    })
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ApiResponse.success("Password changed successfully");
    }
}
