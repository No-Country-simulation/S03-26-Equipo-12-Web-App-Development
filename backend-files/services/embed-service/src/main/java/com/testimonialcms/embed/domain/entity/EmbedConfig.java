package com.testimonialcms.embed.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "embed_configs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmbedConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String name;

    private String categoryFilter;    // slug de categoría a filtrar
    private String tagFilter;         // slug de tag a filtrar

    @Column(nullable = false)
    @Builder.Default
    private String layout = "grid";   // grid | list | carousel

    @Column(nullable = false)
    @Builder.Default
    private Integer maxItems = 6;

    @Column(nullable = false)
    @Builder.Default
    private Boolean showRating = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean showAvatar = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt  = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
