package com.rutika.inventory.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Update profile request payload")
public class UpdateProfileRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Schema(description = "User's first name", example = "John")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be exactly 10 digits")
    @Schema(description = "User's phone number (10 digits)", example = "9876543210")
    private String phone;
}
