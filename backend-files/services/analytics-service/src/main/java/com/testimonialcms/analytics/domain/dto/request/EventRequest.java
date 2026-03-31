package com.testimonialcms.analytics.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class EventRequest {

    @NotNull(message = "El testimonialId es requerido")
    private UUID testimonialId;

    @NotBlank(message = "El tipo de evento es requerido")
    private String eventType;   // VIEW, CLICK, SHARE, EMBED_VIEW

    private String source;      // direct, embed, api
}
