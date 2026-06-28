package com.rutika.inventory.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Update user request payload")
public class UpdateUserRequest {

    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Schema(description = "User's first name", example = "John")
    private String firstName;

    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @Schema(description = "User's phone number", example = "+1234567890")
    private String phone;

    @Schema(description = "Role name (e.g. SUPER_ADMIN, ADMIN)", example = "ADMIN")
    private String roleName;

    @Schema(description = "User account status", example = "ACTIVE")
    private String status;
}
