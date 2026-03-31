package com.testimonialcms.testimonial.domain.dto.request;

import com.testimonialcms.shared.enums.TestimonialType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class TestimonialUpdateRequest {

    @Size(max = 500)
    private String title;

    private String content;

    private TestimonialType type;

    private String authorName;
    private String authorRole;
    private String authorCompany;
    private String authorAvatarUrl;

    private UUID categoryId;

    private Set<UUID> tagIds;

    private Boolean featured;

    @Min(0) @Max(5)
    private Integer rating;
}
