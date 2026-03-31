package com.testimonialcms.testimonial.domain.dto.request;

import com.testimonialcms.shared.enums.TestimonialStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ModerationRequest {

    @NotNull(message = "El estado es requerido")
    private TestimonialStatus status;

    private String reason;
}
