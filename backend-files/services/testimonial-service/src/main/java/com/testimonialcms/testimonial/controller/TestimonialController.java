package com.testimonialcms.testimonial.controller;

import com.testimonialcms.shared.dto.response.ApiResponse;
import com.testimonialcms.shared.dto.response.PagedResponse;
import com.testimonialcms.shared.enums.TestimonialStatus;
import com.testimonialcms.testimonial.domain.dto.request.ModerationRequest;
import com.testimonialcms.testimonial.domain.dto.request.TestimonialCreateRequest;
import com.testimonialcms.testimonial.domain.dto.request.TestimonialUpdateRequest;
import com.testimonialcms.testimonial.domain.dto.response.TestimonialResponse;
import com.testimonialcms.testimonial.service.TestimonialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/testimonials")
@RequiredArgsConstructor
@Tag(name = "Testimonials", description = "Gestión de testimonios")
public class TestimonialController {

    private final TestimonialService testimonialService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Crear testimonio")
    public ResponseEntity<ApiResponse<TestimonialResponse>> create(
            @Valid @RequestBody TestimonialCreateRequest request,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Testimonio creado",
                        testimonialService.create(request, UUID.fromString(userId))));
    }

    @GetMapping
    @Operation(summary = "Listar testimonios con filtros")
    public ResponseEntity<ApiResponse<PagedResponse<TestimonialResponse>>> findAll(
            @RequestParam(required = false) TestimonialStatus status,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                testimonialService.findAll(status, categoryId, type,
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/search")
    @Operation(summary = "Búsqueda por texto")
    public ResponseEntity<ApiResponse<PagedResponse<TestimonialResponse>>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                testimonialService.search(q, PageRequest.of(page, size))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener testimonio por ID")
    public ResponseEntity<ApiResponse<TestimonialResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(testimonialService.findById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Actualizar testimonio")
    public ResponseEntity<ApiResponse<TestimonialResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody TestimonialUpdateRequest request,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(ApiResponse.ok("Testimonio actualizado",
                testimonialService.update(id, request, UUID.fromString(userId))));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Moderar / cambiar estado")
    public ResponseEntity<ApiResponse<TestimonialResponse>> moderate(
            @PathVariable UUID id,
            @Valid @RequestBody ModerationRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Estado actualizado",
                testimonialService.moderate(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar testimonio")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        testimonialService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
