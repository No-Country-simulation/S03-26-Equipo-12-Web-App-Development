package com.testimonialcms.auth.mapper;

import com.testimonialcms.auth.domain.dto.response.UserResponse;
import com.testimonialcms.auth.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    // @Mapping(target = ".", source = "user") // Mapeo general
    //@Mapping(target = "passwordHash", ignore = true) // Esto solo funciona si UserResponse TIENE el campo
    UserResponse toResponse(User user);
}
