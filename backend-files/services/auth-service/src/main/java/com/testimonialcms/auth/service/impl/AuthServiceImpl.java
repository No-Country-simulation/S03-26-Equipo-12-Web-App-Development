package com.testimonialcms.auth.service.impl;

import com.testimonialcms.auth.domain.dto.request.LoginRequest;
import com.testimonialcms.auth.domain.dto.request.RefreshTokenRequest;
import com.testimonialcms.auth.domain.dto.response.LoginResponse;
import com.testimonialcms.auth.domain.entity.RefreshToken;
import com.testimonialcms.auth.domain.entity.User;
import com.testimonialcms.auth.repository.RefreshTokenRepository;
import com.testimonialcms.auth.repository.UserRepository;
import com.testimonialcms.auth.service.AuthService;
import com.testimonialcms.shared.exception.ResourceNotFoundException;
import com.testimonialcms.shared.exception.UnauthorizedException;
import com.testimonialcms.shared.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository        userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder        passwordEncoder;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    @Value("${jwt.refresh-expiration-days:7}")
    private int refreshExpirationDays;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));

        if (!user.getActive()) {
            throw new UnauthorizedException("Usuario inactivo");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        String accessToken = generateAccessToken(user);
        String refreshTokenValue = createRefreshToken(user);

        return buildLoginResponse(user, accessToken, refreshTokenValue);
    }

    @Override
    @Transactional
    public LoginResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Refresh token inválido"));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new UnauthorizedException("Refresh token expirado. Por favor inicie sesión nuevamente");
        }

        User user = refreshToken.getUser();
        refreshTokenRepository.delete(refreshToken);

        String newAccessToken  = generateAccessToken(user);
        String newRefreshToken = createRefreshToken(user);

        return buildLoginResponse(user, newAccessToken, newRefreshToken);
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }

    // ── Helpers ────────────────────────────────────────────────

    private String generateAccessToken(User user) {
        return JwtUtil.generateToken(
                jwtSecret,
                jwtExpirationMs,
                user.getId().toString(),
                Map.of(
                        "email", user.getEmail(),
                        "name",  user.getName(),
                        "role",  user.getRole().name()
                )
        );
    }

    private String createRefreshToken(User user) {
        String tokenValue = JwtUtil.generateRefreshToken();
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .token(tokenValue)
                .expiresAt(LocalDateTime.now().plusDays(refreshExpirationDays))
                .build();
        refreshTokenRepository.save(token);
        return tokenValue;
    }

    private LoginResponse buildLoginResponse(User user, String access, String refresh) {
        return LoginResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .user(LoginResponse.UserInfo.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .name(user.getName())
                        .role(user.getRole())
                        .build())
                .build();
    }
}
