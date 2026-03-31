package com.testimonialcms.testimonial.mapper;

import com.testimonialcms.testimonial.domain.dto.response.TestimonialResponse;
import com.testimonialcms.testimonial.domain.entity.Category;
import com.testimonialcms.testimonial.domain.entity.Tag;
import com.testimonialcms.testimonial.domain.entity.Testimonial;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TestimonialMapper {

    TestimonialResponse toResponse(Testimonial testimonial);

    TestimonialResponse.CategoryResponse toCategoryResponse(Category category);

    TestimonialResponse.TagResponse toTagResponse(Tag tag);
}
