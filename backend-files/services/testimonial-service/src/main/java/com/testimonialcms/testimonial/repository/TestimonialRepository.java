package com.testimonialcms.testimonial.repository;

import com.testimonialcms.shared.enums.TestimonialStatus;
import com.testimonialcms.testimonial.domain.entity.Testimonial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TestimonialRepository extends JpaRepository<Testimonial, UUID> {

    Page<Testimonial> findByStatus(TestimonialStatus status, Pageable pageable);

    Page<Testimonial> findByUserId(UUID userId, Pageable pageable);

    Page<Testimonial> findByCategoryId(UUID categoryId, Pageable pageable);

    Page<Testimonial> findByFeaturedTrue(Pageable pageable);

    @Query("""
        SELECT t FROM Testimonial t
        WHERE (:status IS NULL OR t.status = :status)
          AND (:categoryId IS NULL OR t.category.id = :categoryId)
          AND (:type IS NULL OR t.type = :type)
        ORDER BY t.createdAt DESC
        """)
    Page<Testimonial> findAllFiltered(
            @Param("status")     TestimonialStatus status,
            @Param("categoryId") UUID categoryId,
            @Param("type")       String type,
            Pageable pageable);

    @Query("""
        SELECT t FROM Testimonial t
        WHERE t.status = 'PUBLISHED'
          AND (LOWER(t.title)      LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(t.content)    LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(t.authorName) LIKE LOWER(CONCAT('%', :q, '%')))
        """)
    Page<Testimonial> search(@Param("q") String query, Pageable pageable);
}
