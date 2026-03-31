package com.testimonialcms.embed.controller;

import com.testimonialcms.embed.domain.dto.request.EmbedConfigRequest;
import com.testimonialcms.embed.domain.dto.response.EmbedConfigResponse;
import com.testimonialcms.embed.service.EmbedService;
import com.testimonialcms.shared.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/embed")
@RequiredArgsConstructor
@Tag(name = "Embed", description = "Gestión de widgets embed")
public class EmbedController {

    private final EmbedService embedService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Crear configuración de embed")
    public ResponseEntity<ApiResponse<EmbedConfigResponse>> create(
            @Valid @RequestBody EmbedConfigRequest request,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Widget creado",
                        embedService.create(request, UUID.fromString(userId))));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Listar widgets del usuario")
    public ResponseEntity<ApiResponse<List<EmbedConfigResponse>>> findMine(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(ApiResponse.ok(
                embedService.findByUser(UUID.fromString(userId))));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Obtener widget por ID")
    public ResponseEntity<ApiResponse<EmbedConfigResponse>> findById(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(embedService.findById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Actualizar widget")
    public ResponseEntity<ApiResponse<EmbedConfigResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody EmbedConfigRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Widget actualizado",
                embedService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar widget")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        embedService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
