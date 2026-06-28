package com.rutika.inventory.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Refresh token response with new access token")
public class RefreshTokenResponse {

    @Schema(description = "New JWT access token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;

    @Schema(description = "Access token expiration time in seconds", example = "900")
    private long expiresIn;
}
