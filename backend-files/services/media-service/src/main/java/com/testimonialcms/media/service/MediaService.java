package com.testimonialcms.media.service;

import com.testimonialcms.media.domain.dto.response.MediaAssetResponse;
import com.testimonialcms.media.domain.entity.MediaAsset;
import com.testimonialcms.media.repository.MediaAssetRepository;
import com.testimonialcms.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaAssetRepository mediaAssetRepository;
    private final CloudinaryService    cloudinaryService;
    private final YouTubeService       youTubeService;

    @Transactional
    public MediaAssetResponse uploadImage(MultipartFile file, UUID testimonialId) {
        Map<String, String> result = cloudinaryService.uploadImage(file);

        MediaAsset asset = MediaAsset.builder()
                .testimonialId(testimonialId)
                .type("IMAGE")
                .url(result.get("url"))
                .cloudinaryId(result.get("cloudinaryId"))
                .build();

        return toResponse(mediaAssetRepository.save(asset));
    }

    @Transactional
    public MediaAssetResponse uploadVideo(MultipartFile file, UUID testimonialId) {
        Map<String, String> result = cloudinaryService.uploadVideo(file);

        MediaAsset asset = MediaAsset.builder()
                .testimonialId(testimonialId)
                .type("VIDEO")
                .url(result.get("url"))
                .cloudinaryId(result.get("cloudinaryId"))
                .thumbnailUrl(result.get("thumbnailUrl"))
                .build();

        return toResponse(mediaAssetRepository.save(asset));
    }

    @Transactional
    public MediaAssetResponse linkYouTube(String youtubeUrl, UUID testimonialId) {
        String videoId = youTubeService.extractVideoId(youtubeUrl);
        Map<String, String> meta = youTubeService.getVideoMetadata(videoId);

        MediaAsset asset = MediaAsset.builder()
                .testimonialId(testimonialId)
                .type("VIDEO")
                .url(meta.get("embedUrl"))
                .youtubeId(videoId)
                .thumbnailUrl(meta.get("thumbnailUrl"))
                .build();

        return toResponse(mediaAssetRepository.save(asset));
    }

    @Transactional(readOnly = true)
    public List<MediaAssetResponse> findByTestimonialId(UUID testimonialId) {
        return mediaAssetRepository.findByTestimonialId(testimonialId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public void delete(UUID id) {
        MediaAsset asset = mediaAssetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MediaAsset", "id", id));

        if (asset.getCloudinaryId() != null) {
            String resourceType = "IMAGE".equals(asset.getType()) ? "image" : "video";
            cloudinaryService.deleteAsset(asset.getCloudinaryId(), resourceType);
        }

        mediaAssetRepository.delete(asset);
    }

    private MediaAssetResponse toResponse(MediaAsset a) {
        return MediaAssetResponse.builder()
                .id(a.getId())
                .testimonialId(a.getTestimonialId())
                .type(a.getType())
                .url(a.getUrl())
                .cloudinaryId(a.getCloudinaryId())
                .youtubeId(a.getYoutubeId())
                .thumbnailUrl(a.getThumbnailUrl())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
