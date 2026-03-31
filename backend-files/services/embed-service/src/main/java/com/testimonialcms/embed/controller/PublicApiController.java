package com.testimonialcms.embed.controller;

import com.testimonialcms.embed.domain.dto.response.PublicTestimonialResponse;
import com.testimonialcms.embed.service.EmbedService;
import com.testimonialcms.shared.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
@Tag(name = "Public API", description = "API pública sin autenticación para sitios externos")
public class PublicApiController {

    private final EmbedService embedService;

    @GetMapping("/testimonials")
    @Operation(summary = "Listar testimonios publicados — sin auth")
    public ResponseEntity<ApiResponse<List<PublicTestimonialResponse>>> getPublic(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "6") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(
                embedService.getPublicTestimonials(category, tag, limit)));
    }

    /**
     * Devuelve el snippet JS del widget listo para embeber en cualquier sitio.
     * Uso: <script src="http://gateway:8080/public/widget/{id}.js"></script>
     */
    @GetMapping(value = "/widget/{widgetId}.js",
                produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Script JS del widget embed")
    public ResponseEntity<String> getWidgetScript(@PathVariable UUID widgetId) {
        String js = buildWidgetScript(widgetId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/javascript"))
                .body(js);
    }

    // ── Helpers ───────────────────────────────────────────────

    private String buildWidgetScript(UUID widgetId) {
        return """
                (function() {
                  const WIDGET_ID = '%s';
                  const API_BASE  = document.currentScript.src
                    .replace('/public/widget/' + WIDGET_ID + '.js', '');

                  fetch(API_BASE + '/public/testimonials?widgetId=' + WIDGET_ID)
                    .then(r => r.json())
                    .then(data => {
                      const container = document.querySelector(
                        '[data-widget-id="' + WIDGET_ID + '"]'
                      );
                      if (!container || !data.data) return;
                      container.innerHTML = data.data.map(t => `
                        <div class="tcms-testimonial">
                          <p class="tcms-content">${t.content}</p>
                          <span class="tcms-author">${t.authorName}</span>
                          ${t.authorRole ? `<span class="tcms-role">${t.authorRole}</span>` : ''}
                        </div>
                      `).join('');
                    })
                    .catch(console.error);
                })();
                """.formatted(widgetId);
    }
}
