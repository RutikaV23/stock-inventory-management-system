package com.rutika.inventory.service.impl;

import com.rutika.inventory.dto.request.ChangePasswordRequest;
import com.rutika.inventory.dto.request.LoginRequest;
import com.rutika.inventory.dto.request.RefreshTokenRequest;
import com.rutika.inventory.dto.request.UpdateProfileRequest;
import com.rutika.inventory.dto.response.LoginResponse;
import com.rutika.inventory.dto.response.RefreshTokenResponse;
import com.rutika.inventory.dto.response.UserProfileResponse;
import com.rutika.inventory.entity.RefreshToken;
import com.rutika.inventory.entity.User;
import com.rutika.inventory.enums.UserStatus;
import com.rutika.inventory.exception.BadRequestException;
import com.rutika.inventory.exception.ResourceNotFoundException;
import com.rutika.inventory.repository.RefreshTokenRepository;
import com.rutika.inventory.repository.UserRepository;
import com.rutika.inventory.security.JwtService;
import com.rutika.inventory.service.interfaces.AuthService;
import com.rutika.inventory.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtil securityUtil;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BadRequestException("Account is not active");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        refreshTokenRepository.deleteByUserId(user.getId());

        String roleName = user.getRole() != null ? user.getRole().getRoleName() : "ADMIN";
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), roleName);
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setExpiresAt(Instant.now().plusMillis(604800000));
        refreshTokenEntity.setRevoked(false);
        refreshTokenRepository.save(refreshTokenEntity);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getAccessTokenExpiration() / 1000)
                .role(roleName)
                .user(toUserProfileResponse(user))
                .build();
    }

    @Override
    @Transactional
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        if (storedToken.isRevoked()) {
            throw new BadRequestException("Refresh token has been revoked");
        }

        if (storedToken.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Refresh token has expired");
        }

        User user = storedToken.getUser();
        String roleName = user.getRole() != null ? user.getRole().getRoleName() : "ADMIN";
        String newAccessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), roleName);

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .expiresIn(jwtService.getAccessTokenExpiration() / 1000)
                .build();
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile() {
        String userId = securityUtil.getCurrentUser().getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return toUserProfileResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(UpdateProfileRequest request) {
        String userId = securityUtil.getCurrentUser().getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        userRepository.save(user);
        return toUserProfileResponse(user);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        String userId = securityUtil.getCurrentUser().getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private UserProfileResponse toUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole() != null ? user.getRole().getRoleName() : null)
                .status(user.getStatus())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
