package com.rutika.inventory.dto.response;

import com.rutika.inventory.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@Schema(description = "User profile response")
public class UserProfileResponse {

    @Schema(description = "User ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String id;

    @Schema(description = "User's first name", example = "John")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @Schema(description = "User's email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "User's phone number", example = "+1234567890")
    private String phone;

    @Schema(description = "User account status", example = "ACTIVE")
    private UserStatus status;

    @Schema(description = "Last login timestamp", example = "2026-06-27T10:30:00Z")
    private Instant lastLoginAt;

    @Schema(description = "Account creation timestamp", example = "2026-06-27T10:30:00Z")
    private Instant createdAt;
}
