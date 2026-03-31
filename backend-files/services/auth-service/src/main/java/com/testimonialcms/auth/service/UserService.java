package com.testimonialcms.auth.service;

import com.testimonialcms.auth.domain.dto.request.RegisterRequest;
import com.testimonialcms.auth.domain.dto.response.UserResponse;
import com.testimonialcms.shared.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    UserResponse create(RegisterRequest request);

    UserResponse findById(UUID id);

    PagedResponse<UserResponse> findAll(Pageable pageable);

    UserResponse update(UUID id, RegisterRequest request);

    void toggleActive(UUID id);
}
