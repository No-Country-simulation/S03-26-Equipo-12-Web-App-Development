package com.testimonialcms.media.controller;

import com.testimonialcms.media.domain.dto.response.MediaAssetResponse;
import com.testimonialcms.media.service.MediaService;
import com.testimonialcms.shared.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@Tag(name = "Media", description = "Gestión de archivos multimedia")
public class MediaController {

    private final MediaService mediaService;

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Subir imagen a Cloudinary")
    public ResponseEntity<ApiResponse<MediaAssetResponse>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("testimonialId") UUID testimonialId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Imagen subida", mediaService.uploadImage(file, testimonialId)));
    }

    @PostMapping(value = "/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Subir video a Cloudinary")
    public ResponseEntity<ApiResponse<MediaAssetResponse>> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("testimonialId") UUID testimonialId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Video subido", mediaService.uploadVideo(file, testimonialId)));
    }

    @PostMapping("/youtube")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Vincular video de YouTube")
    public ResponseEntity<ApiResponse<MediaAssetResponse>> linkYouTube(
            @RequestParam("url") String youtubeUrl,
            @RequestParam("testimonialId") UUID testimonialId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("YouTube vinculado",
                        mediaService.linkYouTube(youtubeUrl, testimonialId)));
    }

    @GetMapping("/testimonial/{testimonialId}")
    @Operation(summary = "Obtener assets de un testimonio")
    public ResponseEntity<ApiResponse<List<MediaAssetResponse>>> findByTestimonialId(
            @PathVariable UUID testimonialId) {
        return ResponseEntity.ok(
                ApiResponse.ok(mediaService.findByTestimonialId(testimonialId)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Eliminar asset")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        mediaService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
