package com.testimonialcms.auth.service;

import com.testimonialcms.auth.domain.dto.request.LoginRequest;
import com.testimonialcms.auth.domain.dto.request.RefreshTokenRequest;
import com.testimonialcms.auth.domain.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    LoginResponse refresh(RefreshTokenRequest request);

    void logout(String refreshToken);
}
