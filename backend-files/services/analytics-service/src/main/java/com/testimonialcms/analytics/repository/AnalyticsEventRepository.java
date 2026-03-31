package com.testimonialcms.analytics.repository;

import com.testimonialcms.analytics.domain.entity.AnalyticsEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public interface AnalyticsEventRepository extends JpaRepository<AnalyticsEvent, UUID> {

    List<AnalyticsEvent> findByTestimonialId(UUID testimonialId);

    long countByTestimonialIdAndEventType(UUID testimonialId, String eventType);

    @Query("""
        SELECT e.eventType AS eventType, COUNT(e) AS total
        FROM AnalyticsEvent e
        WHERE e.occurredAt >= :from
        GROUP BY e.eventType
        """)
    List<Object[]> countGroupedByEventType(@Param("from") LocalDateTime from);

    @Query("""
        SELECT e.source AS source, COUNT(e) AS total
        FROM AnalyticsEvent e
        WHERE e.occurredAt >= :from
        GROUP BY e.source
        """)
    List<Object[]> countGroupedBySource(@Param("from") LocalDateTime from);

    @Query("""
        SELECT CAST(e.occurredAt AS date) AS day, COUNT(e) AS total
        FROM AnalyticsEvent e
        WHERE e.occurredAt >= :from
        GROUP BY CAST(e.occurredAt AS date)
        ORDER BY CAST(e.occurredAt AS date)
        """)
    List<Object[]> countGroupedByDay(@Param("from") LocalDateTime from);

    @Query("""
        SELECT e.testimonialId, COUNT(e)
        FROM AnalyticsEvent e
        WHERE e.occurredAt >= :from
        GROUP BY e.testimonialId
        ORDER BY COUNT(e) DESC
        """)
    List<Object[]> findTopTestimonials(@Param("from") LocalDateTime from);

    long countByOccurredAtAfter(LocalDateTime from);
}
