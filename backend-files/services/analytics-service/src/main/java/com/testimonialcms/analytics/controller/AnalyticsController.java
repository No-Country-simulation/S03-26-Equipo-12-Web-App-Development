package com.testimonialcms.analytics.controller;

import com.testimonialcms.analytics.domain.dto.request.EventRequest;
import com.testimonialcms.analytics.domain.dto.response.DashboardResponse;
import com.testimonialcms.analytics.domain.dto.response.TestimonialMetricsResponse;
import com.testimonialcms.analytics.service.AnalyticsService;
import com.testimonialcms.shared.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Métricas y engagement de testimonios")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @PostMapping("/events")
    @Operation(summary = "Registrar evento de engagement (público)")
    public ResponseEntity<ApiResponse<Void>> registerEvent(
            @Valid @RequestBody EventRequest request,
            HttpServletRequest httpRequest) {
        analyticsService.registerEvent(request, httpRequest);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/testimonials/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Métricas de un testimonio específico")
    public ResponseEntity<ApiResponse<TestimonialMetricsResponse>> getMetrics(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getMetrics(id)));
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @Operation(summary = "Dashboard general de analytics")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getDashboard(days)));
    }
}
