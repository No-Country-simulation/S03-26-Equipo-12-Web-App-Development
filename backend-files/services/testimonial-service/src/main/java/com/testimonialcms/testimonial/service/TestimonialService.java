package com.testimonialcms.testimonial.service;

import com.testimonialcms.shared.dto.response.PagedResponse;
import com.testimonialcms.shared.enums.TestimonialStatus;
import com.testimonialcms.testimonial.domain.dto.request.ModerationRequest;
import com.testimonialcms.testimonial.domain.dto.request.TestimonialCreateRequest;
import com.testimonialcms.testimonial.domain.dto.request.TestimonialUpdateRequest;
import com.testimonialcms.testimonial.domain.dto.response.TestimonialResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TestimonialService {

    TestimonialResponse create(TestimonialCreateRequest request, UUID userId);

    TestimonialResponse findById(UUID id);

    PagedResponse<TestimonialResponse> findAll(TestimonialStatus status,
                                               UUID categoryId,
                                               String type,
                                               Pageable pageable);

    PagedResponse<TestimonialResponse> search(String query, Pageable pageable);

    TestimonialResponse update(UUID id, TestimonialUpdateRequest request, UUID userId);

    TestimonialResponse moderate(UUID id, ModerationRequest request);

    void delete(UUID id);
}
