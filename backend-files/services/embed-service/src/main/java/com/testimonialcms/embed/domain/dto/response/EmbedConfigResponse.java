package com.testimonialcms.embed.domain.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class EmbedConfigResponse {

    private UUID    id;
    private String  name;
    private String  categoryFilter;
    private String  tagFilter;
    private String  layout;
    private Integer maxItems;
    private Boolean showRating;
    private Boolean showAvatar;
    private Boolean active;
    private String  scriptTag;    // <script src="..."> listo para copiar
}
