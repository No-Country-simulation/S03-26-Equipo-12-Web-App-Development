package com.testimonialcms.testimonial.repository;

import com.testimonialcms.testimonial.domain.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {

    Optional<Tag> findBySlug(String slug);

    List<Tag> findByIdIn(Set<UUID> ids);

    boolean existsByName(String name);
}
