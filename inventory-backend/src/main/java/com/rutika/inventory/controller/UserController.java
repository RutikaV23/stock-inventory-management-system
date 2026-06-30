package com.rutika.inventory.controller;

import com.rutika.inventory.constants.ApiConstants;
import com.rutika.inventory.dto.request.CreateUserRequest;
import com.rutika.inventory.dto.request.UpdateUserRequest;
import com.rutika.inventory.dto.response.UserResponse;
import com.rutika.inventory.response.ApiResponse;
import com.rutika.inventory.response.PageResponse;
import com.rutika.inventory.security.Permission;
import com.rutika.inventory.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiConstants.USERS_PATH)
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User management endpoints for admin users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Secured("USER_CREATE")
    @Operation(summary = "Create a new user", description = "Creates a new user with encrypted password. Only SUPER_ADMIN can create users.")
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ApiResponse.success("User created successfully", response);
    }

    @GetMapping
    @Secured("USER_READ")
    @Operation(summary = "Get all users", description = "Retrieves all users with pagination, sorting, keyword search, and status filter support")
    public ApiResponse<PageResponse<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = ApiConstants.PAGE_DEFAULT) int page,
            @RequestParam(defaultValue = ApiConstants.SIZE_DEFAULT) int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        PageResponse<UserResponse> response = userService.getAllUsers(page, size, sort, keyword, status);
        return ApiResponse.success("Users retrieved successfully", response);
    }

    @GetMapping(ApiConstants.ID_PATH_VARIABLE)
    @Secured("USER_READ")
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their ID")
    public ApiResponse<UserResponse> getUserById(@PathVariable String id) {
        UserResponse response = userService.getUserById(id);
        return ApiResponse.success("User retrieved successfully", response);
    }

    @PutMapping(ApiConstants.ID_PATH_VARIABLE)
    @Secured("USER_UPDATE")
    @Operation(summary = "Update user", description = "Updates user details. Email cannot be updated.")
    public ApiResponse<UserResponse> updateUser(@PathVariable String id,
                                                 @Valid @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ApiResponse.success("User updated successfully", response);
    }

    @DeleteMapping(ApiConstants.ID_PATH_VARIABLE)
    @Secured("USER_DELETE")
    @Operation(summary = "Delete user", description = "Soft deletes a user by setting status to INACTIVE")
    public ApiResponse<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ApiResponse.success("User deleted successfully");
    }
}
