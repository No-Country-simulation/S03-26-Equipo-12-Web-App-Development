package com.testimonialcms.analytics.domain.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TestimonialMetricsResponse {

    private UUID   testimonialId;
    private long   totalViews;
    private long   totalClicks;
    private long   totalShares;
    private long   totalEmbedViews;
    private double engagementRate;   // clicks / views * 100
}
