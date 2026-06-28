package com.rutika.inventory.service.interfaces;

import com.rutika.inventory.dto.request.ChangePasswordRequest;
import com.rutika.inventory.dto.request.LoginRequest;
import com.rutika.inventory.dto.request.RefreshTokenRequest;
import com.rutika.inventory.dto.request.UpdateProfileRequest;
import com.rutika.inventory.dto.response.LoginResponse;
import com.rutika.inventory.dto.response.RefreshTokenResponse;
import com.rutika.inventory.dto.response.UserProfileResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    RefreshTokenResponse refreshToken(RefreshTokenRequest request);

    void logout(String refreshToken);

    UserProfileResponse getProfile();

    UserProfileResponse updateProfile(UpdateProfileRequest request);

    void changePassword(ChangePasswordRequest request);
}
