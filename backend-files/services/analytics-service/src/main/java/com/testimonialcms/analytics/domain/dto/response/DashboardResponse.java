package com.testimonialcms.analytics.domain.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardResponse {

    private long totalEvents;
    private long totalViews;
    private long totalClicks;
    private long totalShares;
    private long totalEmbedViews;

    private Map<String, Long> eventsByType;     // { "VIEW": 120, "CLICK": 45 }
    private Map<String, Long> eventsBySource;   // { "direct": 80, "embed": 40 }
    private Map<String, Long> eventsByDay;      // { "2025-01-01": 12, ... }

    private List<TestimonialMetricsResponse> topTestimonials;
}
