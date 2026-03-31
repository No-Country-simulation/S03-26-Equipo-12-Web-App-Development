package com.testimonialcms.embed.domain.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PublicTestimonialResponse {

    private UUID   id;
    private String title;
    private String content;
    private String type;
    private String authorName;
    private String authorRole;
    private String authorCompany;
    private String authorAvatarUrl;
    private Integer rating;
    private String  categoryName;
    private List<String> tagNames;
    private LocalDateTime publishedAt;
}
