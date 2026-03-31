package com.testimonialcms.embed.repository;

import com.testimonialcms.embed.domain.entity.EmbedConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmbedConfigRepository extends JpaRepository<EmbedConfig, UUID> {

    List<EmbedConfig> findByUserId(UUID userId);

    List<EmbedConfig> findByActiveTrue();
}
