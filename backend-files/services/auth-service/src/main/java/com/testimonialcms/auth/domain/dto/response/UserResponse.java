package com.testimonialcms.auth.domain.dto.response;

import com.testimonialcms.shared.enums.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserResponse {

    private UUID id;
    private String email;
    private String name;
    private UserRole role;
    private Boolean active;
    private LocalDateTime createdAt;
}
