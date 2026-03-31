package com.testimonialcms.media.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class YouTubeService {

    @Value("${youtube.api-key}")
    private String apiKey;

    private static final String API_URL =
            "https://www.googleapis.com/youtube/v3/videos?id=%s&key=%s&part=snippet";

    private static final Pattern YT_PATTERN = Pattern.compile(
            "(?:youtube\\.com/watch\\?v=|youtu\\.be/)([a-zA-Z0-9_-]{11})"
    );

    public String extractVideoId(String url) {
        Matcher m = YT_PATTERN.matcher(url);
        if (m.find()) return m.group(1);
        throw new IllegalArgumentException("URL de YouTube inválida: " + url);
    }

    public Map<String, String> getVideoMetadata(String videoId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String endpoint = String.format(API_URL, videoId, apiKey);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(endpoint, Map.class);

            if (response == null) return buildEmbedOnly(videoId);

            @SuppressWarnings("unchecked")
            var items = (java.util.List<?>) response.get("items");
            if (items == null || items.isEmpty()) return buildEmbedOnly(videoId);

            @SuppressWarnings("unchecked")
            var snippet = (Map<String, Object>)
                    ((Map<String, Object>) items.get(0)).get("snippet");

            @SuppressWarnings("unchecked")
            var thumbnails = (Map<String, Object>) snippet.get("thumbnails");
            @SuppressWarnings("unchecked")
            var high = (Map<String, String>) thumbnails.get("high");

            return Map.of(
                    "videoId",      videoId,
                    "title",        (String) snippet.get("title"),
                    "thumbnailUrl", high.get("url"),
                    "embedUrl",     "https://www.youtube.com/embed/" + videoId
            );
        } catch (Exception e) {
            log.warn("No se pudo obtener metadata de YouTube: {}", e.getMessage());
            return buildEmbedOnly(videoId);
        }
    }

    private Map<String, String> buildEmbedOnly(String videoId) {
        return Map.of(
                "videoId",      videoId,
                "thumbnailUrl", "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg",
                "embedUrl",     "https://www.youtube.com/embed/" + videoId
        );
    }
}
