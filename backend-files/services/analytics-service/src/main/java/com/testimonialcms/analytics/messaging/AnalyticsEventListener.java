package com.testimonialcms.analytics.messaging;

import com.testimonialcms.analytics.domain.entity.AnalyticsEvent;
import com.testimonialcms.analytics.repository.AnalyticsEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyticsEventListener {

    private final AnalyticsEventRepository eventRepository;

    @RabbitListener(queues = "analytics.testimonial.queue")
    public void onTestimonialEvent(String testimonialId) {
        log.info("Evento recibido de testimonial-service: {}", testimonialId);
        try {
            AnalyticsEvent event = AnalyticsEvent.builder()
                    .testimonialId(UUID.fromString(testimonialId))
                    .eventType("SYSTEM")
                    .source("internal")
                    .build();
            eventRepository.save(event);
        } catch (Exception e) {
            log.error("Error procesando evento de testimonio: {}", e.getMessage());
        }
    }
}
