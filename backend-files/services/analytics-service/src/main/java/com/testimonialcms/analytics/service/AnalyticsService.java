package com.testimonialcms.analytics.service;

import com.testimonialcms.analytics.domain.dto.request.EventRequest;
import com.testimonialcms.analytics.domain.dto.response.DashboardResponse;
import com.testimonialcms.analytics.domain.dto.response.TestimonialMetricsResponse;
import com.testimonialcms.analytics.domain.entity.AnalyticsEvent;
import com.testimonialcms.analytics.repository.AnalyticsEventRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsEventRepository eventRepository;

    @Transactional
    public void registerEvent(EventRequest req, HttpServletRequest httpRequest) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .testimonialId(req.getTestimonialId())
                .eventType(req.getEventType())
                .source(req.getSource() != null ? req.getSource() : "direct")
                .userAgent(httpRequest.getHeader("User-Agent"))
                .ipHash(hashIp(httpRequest.getRemoteAddr()))
                .build();
        eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public TestimonialMetricsResponse getMetrics(UUID testimonialId) {
        long views      = eventRepository.countByTestimonialIdAndEventType(testimonialId, "VIEW");
        long clicks     = eventRepository.countByTestimonialIdAndEventType(testimonialId, "CLICK");
        long shares     = eventRepository.countByTestimonialIdAndEventType(testimonialId, "SHARE");
        long embedViews = eventRepository.countByTestimonialIdAndEventType(testimonialId, "EMBED_VIEW");

        double engagement = views > 0 ? (double) clicks / views * 100 : 0.0;

        return TestimonialMetricsResponse.builder()
                .testimonialId(testimonialId)
                .totalViews(views)
                .totalClicks(clicks)
                .totalShares(shares)
                .totalEmbedViews(embedViews)
                .engagementRate(Math.round(engagement * 100.0) / 100.0)
                .build();
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(int days) {
        LocalDateTime from = LocalDateTime.now().minusDays(days);

        long totalEvents     = eventRepository.countByOccurredAtAfter(from);
        long totalViews      = eventRepository.countByTestimonialIdAndEventType(null, "VIEW");
        long totalClicks     = eventRepository.countByTestimonialIdAndEventType(null, "CLICK");
        long totalShares     = eventRepository.countByTestimonialIdAndEventType(null, "SHARE");
        long totalEmbedViews = eventRepository.countByTestimonialIdAndEventType(null, "EMBED_VIEW");

        Map<String, Long> byType   = toMap(eventRepository.countGroupedByEventType(from));
        Map<String, Long> bySource = toMap(eventRepository.countGroupedBySource(from));
        Map<String, Long> byDay    = toMap(eventRepository.countGroupedByDay(from));

        List<TestimonialMetricsResponse> top = eventRepository.findTopTestimonials(from)
                .stream()
                .limit(10)
                .map(row -> getMetrics((UUID) row[0]))
                .collect(Collectors.toList());

        return DashboardResponse.builder()
                .totalEvents(totalEvents)
                .totalViews(byType.getOrDefault("VIEW", 0L))
                .totalClicks(byType.getOrDefault("CLICK", 0L))
                .totalShares(byType.getOrDefault("SHARE", 0L))
                .totalEmbedViews(byType.getOrDefault("EMBED_VIEW", 0L))
                .eventsByType(byType)
                .eventsBySource(bySource)
                .eventsByDay(byDay)
                .topTestimonials(top)
                .build();
    }

    // ── Helpers ───────────────────────────────────────────────

    private Map<String, Long> toMap(List<Object[]> rows) {
        Map<String, Long> map = new LinkedHashMap<>();
        for (Object[] row : rows) {
            map.put(row[0] != null ? row[0].toString() : "unknown",
                    ((Number) row[1]).longValue());
        }
        return map;
    }

    private String hashIp(String ip) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(ip.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.substring(0, 16);
        } catch (Exception e) {
            return "unknown";
        }
    }
}
