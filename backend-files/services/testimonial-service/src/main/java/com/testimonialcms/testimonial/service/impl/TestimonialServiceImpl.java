package com.testimonialcms.testimonial.service.impl;

import com.testimonialcms.shared.dto.response.PagedResponse;
import com.testimonialcms.shared.enums.TestimonialStatus;
import com.testimonialcms.shared.exception.BusinessException;
import com.testimonialcms.shared.exception.ResourceNotFoundException;
import com.testimonialcms.testimonial.domain.dto.request.ModerationRequest;
import com.testimonialcms.testimonial.domain.dto.request.TestimonialCreateRequest;
import com.testimonialcms.testimonial.domain.dto.request.TestimonialUpdateRequest;
import com.testimonialcms.testimonial.domain.dto.response.TestimonialResponse;
import com.testimonialcms.testimonial.domain.entity.Category;
import com.testimonialcms.testimonial.domain.entity.Tag;
import com.testimonialcms.testimonial.domain.entity.Testimonial;
import com.testimonialcms.testimonial.mapper.TestimonialMapper;
import com.testimonialcms.testimonial.repository.CategoryRepository;
import com.testimonialcms.testimonial.repository.TagRepository;
import com.testimonialcms.testimonial.repository.TestimonialRepository;
import com.testimonialcms.testimonial.service.TestimonialService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TestimonialServiceImpl implements TestimonialService {

    private final TestimonialRepository testimonialRepository;
    private final CategoryRepository    categoryRepository;
    private final TagRepository         tagRepository;
    private final TestimonialMapper     testimonialMapper;
    private final RabbitTemplate        rabbitTemplate;

    @Override
    @Transactional
    public TestimonialResponse create(TestimonialCreateRequest req, UUID userId) {
        Testimonial testimonial = Testimonial.builder()
                .userId(userId)
                .title(req.getTitle())
                .content(req.getContent())
                .type(req.getType())
                .authorName(req.getAuthorName())
                .authorRole(req.getAuthorRole())
                .authorCompany(req.getAuthorCompany())
                .authorAvatarUrl(req.getAuthorAvatarUrl())
                .featured(req.getFeatured() != null ? req.getFeatured() : false)
                .rating(req.getRating()   != null ? req.getRating()   : 0)
                .build();

        if (req.getCategoryId() != null) {
            Category cat = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", req.getCategoryId()));
            testimonial.setCategory(cat);
        }

        if (req.getTagIds() != null && !req.getTagIds().isEmpty()) {
            List<Tag> tags = tagRepository.findByIdIn(req.getTagIds());
            testimonial.setTags(new HashSet<>(tags));
        }

        Testimonial saved = testimonialRepository.save(testimonial);

        // Notificar al search-service vía RabbitMQ
        rabbitTemplate.convertAndSend("testimonial.events", "testimonial.created",
                saved.getId().toString());

        return testimonialMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TestimonialResponse findById(UUID id) {
        return testimonialMapper.toResponse(
                testimonialRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Testimonio", "id", id))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TestimonialResponse> findAll(TestimonialStatus status,
                                                       UUID categoryId,
                                                       String type,
                                                       Pageable pageable) {
        return PagedResponse.from(
                testimonialRepository.findAllFiltered(status, categoryId, type, pageable)
                        .map(testimonialMapper::toResponse)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TestimonialResponse> search(String query, Pageable pageable) {
        return PagedResponse.from(
                testimonialRepository.search(query, pageable)
                        .map(testimonialMapper::toResponse)
        );
    }

    @Override
    @Transactional
    public TestimonialResponse update(UUID id, TestimonialUpdateRequest req, UUID userId) {
        Testimonial t = testimonialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Testimonio", "id", id));

        if (req.getTitle()           != null) t.setTitle(req.getTitle());
        if (req.getContent()         != null) t.setContent(req.getContent());
        if (req.getType()            != null) t.setType(req.getType());
        if (req.getAuthorName()      != null) t.setAuthorName(req.getAuthorName());
        if (req.getAuthorRole()      != null) t.setAuthorRole(req.getAuthorRole());
        if (req.getAuthorCompany()   != null) t.setAuthorCompany(req.getAuthorCompany());
        if (req.getAuthorAvatarUrl() != null) t.setAuthorAvatarUrl(req.getAuthorAvatarUrl());
        if (req.getFeatured()        != null) t.setFeatured(req.getFeatured());
        if (req.getRating()          != null) t.setRating(req.getRating());

        if (req.getCategoryId() != null) {
            Category cat = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", req.getCategoryId()));
            t.setCategory(cat);
        }

        if (req.getTagIds() != null) {
            t.setTags(new HashSet<>(tagRepository.findByIdIn(req.getTagIds())));
        }

        return testimonialMapper.toResponse(testimonialRepository.save(t));
    }

    @Override
    @Transactional
    public TestimonialResponse moderate(UUID id, ModerationRequest req) {
        Testimonial t = testimonialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Testimonio", "id", id));

        t.setStatus(req.getStatus());
        if (req.getStatus() == TestimonialStatus.PUBLISHED) {
            t.setPublishedAt(LocalDateTime.now());
        }

        Testimonial saved = testimonialRepository.save(t);

        // Notificar cambio de estado
        rabbitTemplate.convertAndSend("testimonial.events", "testimonial.status_changed",
                saved.getId().toString());

        return testimonialMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!testimonialRepository.existsById(id)) {
            throw new ResourceNotFoundException("Testimonio", "id", id);
        }
        testimonialRepository.deleteById(id);
        rabbitTemplate.convertAndSend("testimonial.events", "testimonial.deleted", id.toString());
    }
}
