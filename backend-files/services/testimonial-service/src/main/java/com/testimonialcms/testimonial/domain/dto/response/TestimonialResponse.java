package com.testimonialcms.testimonial.domain.dto.response;

import com.testimonialcms.shared.enums.TestimonialStatus;
import com.testimonialcms.shared.enums.TestimonialType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class TestimonialResponse {

    private UUID   id;
    private UUID   userId;
    private String title;
    private String content;
    private TestimonialType   type;
    private TestimonialStatus status;
    private String authorName;
    private String authorRole;
    private String authorCompany;
    private String authorAvatarUrl;
    private Boolean featured;
    private Integer rating;

    private CategoryResponse category;
    private Set<TagResponse>  tags;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;

    @Data @Builder
    public static class CategoryResponse {
        private UUID   id;
        private String name;
        private String slug;
    }

    @Data @Builder
    public static class TagResponse {
        private UUID   id;
        private String name;
        private String slug;
    }
}
