package com.testimonialcms.testimonial.domain.dto.request;

import com.testimonialcms.shared.enums.TestimonialType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class TestimonialCreateRequest {

    @NotBlank(message = "El título es requerido")
    @Size(max = 500)
    private String title;

    private String content;

    @NotNull(message = "El tipo es requerido")
    private TestimonialType type;

    @NotBlank(message = "El nombre del autor es requerido")
    private String authorName;

    private String authorRole;
    private String authorCompany;
    private String authorAvatarUrl;

    private UUID categoryId;

    private Set<UUID> tagIds;

    private Boolean featured = false;

    @Min(0) @Max(5)
    private Integer rating = 0;
}
