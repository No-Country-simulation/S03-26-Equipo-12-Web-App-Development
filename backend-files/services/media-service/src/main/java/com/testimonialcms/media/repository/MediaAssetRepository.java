package com.testimonialcms.media.repository;

import com.testimonialcms.media.domain.entity.MediaAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MediaAssetRepository extends JpaRepository<MediaAsset, UUID> {

    List<MediaAsset> findByTestimonialId(UUID testimonialId);

    void deleteByTestimonialId(UUID testimonialId);
}
