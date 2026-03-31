package com.testimonialcms.analytics.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "analytics_events")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AnalyticsEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID testimonialId;

    @Column(nullable = false)
    private String eventType;   // VIEW, CLICK, SHARE, EMBED_VIEW

    private String source;      // direct, embed, api

    private String userAgent;
    private String ipHash;      // hash de IP para privacidad

    @Column(nullable = false, updatable = false)
    private LocalDateTime occurredAt;

    @PrePersist
    protected void onCreate() {
        occurredAt = LocalDateTime.now();
    }
}
