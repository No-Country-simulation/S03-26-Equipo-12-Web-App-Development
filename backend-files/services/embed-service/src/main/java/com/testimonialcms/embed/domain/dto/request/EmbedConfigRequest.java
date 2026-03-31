package com.testimonialcms.embed.domain.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmbedConfigRequest {

    @NotBlank(message = "El nombre es requerido")
    private String name;

    private String categoryFilter;
    private String tagFilter;

    private String layout = "grid";

    @Min(1) @Max(20)
    private Integer maxItems = 6;

    private Boolean showRating = true;
    private Boolean showAvatar = true;
}
