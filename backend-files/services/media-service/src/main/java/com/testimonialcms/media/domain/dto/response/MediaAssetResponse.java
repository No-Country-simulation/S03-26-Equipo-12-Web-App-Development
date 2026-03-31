package com.testimonialcms.media.domain.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class MediaAssetResponse {
    private UUID   id;
    private UUID   testimonialId;
    private String type;
    private String url;
    private String cloudinaryId;
    private String youtubeId;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
}
