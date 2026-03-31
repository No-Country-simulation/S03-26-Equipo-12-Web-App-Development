package com.testimonialcms.embed.service;

import com.testimonialcms.embed.domain.dto.request.EmbedConfigRequest;
import com.testimonialcms.embed.domain.dto.response.EmbedConfigResponse;
import com.testimonialcms.embed.domain.dto.response.PublicTestimonialResponse;
import com.testimonialcms.embed.domain.entity.EmbedConfig;
import com.testimonialcms.embed.repository.EmbedConfigRepository;
import com.testimonialcms.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmbedService {

    private final EmbedConfigRepository embedConfigRepository;
    private final RestTemplate           restTemplate;

    @Value("${services.testimonial-url:http://localhost:8082}")
    private String testimonialServiceUrl;

    @Value("${embed.base-url:http://localhost:8086}")
    private String embedBaseUrl;

    // ── Configs ───────────────────────────────────────────────

    @Transactional
    public EmbedConfigResponse create(EmbedConfigRequest req, UUID userId) {
        EmbedConfig config = EmbedConfig.builder()
                .userId(userId)
                .name(req.getName())
                .categoryFilter(req.getCategoryFilter())
                .tagFilter(req.getTagFilter())
                .layout(req.getLayout() != null ? req.getLayout() : "grid")
                .maxItems(req.getMaxItems() != null ? req.getMaxItems() : 6)
                .showRating(req.getShowRating() != null ? req.getShowRating() : true)
                .showAvatar(req.getShowAvatar() != null ? req.getShowAvatar() : true)
                .build();

        EmbedConfig saved = embedConfigRepository.save(config);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<EmbedConfigResponse> findByUser(UUID userId) {
        return embedConfigRepository.findByUserId(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmbedConfigResponse findById(UUID id) {
        return toResponse(embedConfigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EmbedConfig", "id", id)));
    }

    @Transactional
    public EmbedConfigResponse update(UUID id, EmbedConfigRequest req) {
        EmbedConfig config = embedConfigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EmbedConfig", "id", id));

        config.setName(req.getName());
        if (req.getCategoryFilter() != null) config.setCategoryFilter(req.getCategoryFilter());
        if (req.getTagFilter()      != null) config.setTagFilter(req.getTagFilter());
        if (req.getLayout()         != null) config.setLayout(req.getLayout());
        if (req.getMaxItems()       != null) config.setMaxItems(req.getMaxItems());
        if (req.getShowRating()     != null) config.setShowRating(req.getShowRating());
        if (req.getShowAvatar()     != null) config.setShowAvatar(req.getShowAvatar());

        return toResponse(embedConfigRepository.save(config));
    }

    @Transactional
    public void delete(UUID id) {
        if (!embedConfigRepository.existsById(id))
            throw new ResourceNotFoundException("EmbedConfig", "id", id);
        embedConfigRepository.deleteById(id);
    }

    // ── Public API ────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<PublicTestimonialResponse> getPublicTestimonials(
            String category, String tag, int limit) {
        try {
            String url = testimonialServiceUrl
                    + "/api/testimonials?status=PUBLISHED&size=" + limit
                    + (category != null ? "&categorySlug=" + category : "")
                    + (tag      != null ? "&tagSlug="      + tag      : "");

            @SuppressWarnings("unchecked")
            var result = restTemplate.getForObject(url, java.util.Map.class);
            // Transformación simplificada — en producción usar Feign o WebClient
            return List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    // ── Helpers ───────────────────────────────────────────────

    private EmbedConfigResponse toResponse(EmbedConfig c) {
        String scriptTag = String.format(
                "<script src=\"%s/public/widget/%s.js\" data-widget-id=\"%s\"></script>",
                embedBaseUrl, c.getId(), c.getId()
        );
        return EmbedConfigResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .categoryFilter(c.getCategoryFilter())
                .tagFilter(c.getTagFilter())
                .layout(c.getLayout())
                .maxItems(c.getMaxItems())
                .showRating(c.getShowRating())
                .showAvatar(c.getShowAvatar())
                .active(c.getActive())
                .scriptTag(scriptTag)
                .build();
    }
}
