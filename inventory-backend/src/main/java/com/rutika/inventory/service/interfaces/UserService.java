package com.rutika.inventory.service.interfaces;

import com.rutika.inventory.dto.request.CreateUserRequest;
import com.rutika.inventory.dto.request.UpdateUserRequest;
import com.rutika.inventory.dto.response.UserResponse;
import com.rutika.inventory.response.PageResponse;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    PageResponse<UserResponse> getAllUsers(int page, int size, String sort, String keyword);

    UserResponse getUserById(String id);

    UserResponse updateUser(String id, UpdateUserRequest request);

    void deleteUser(String id);
}
