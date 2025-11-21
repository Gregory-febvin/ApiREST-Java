package com.letocart.java_apirest_2026.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
public class GeocodingService {
    private final RestTemplate restTemplate;
    private final String urlTemplate;

    public GeocodingService(RestTemplate restTemplate, @Value("${geocoding.url-template}") String urlTemplate) {
        this.restTemplate = restTemplate;
        this.urlTemplate = urlTemplate;
    }

    /**
     * Interroge le service de géocodage et retourne true si l'adresse a au moins un résultat
     * et qu'un score de correspondance est supérieur au seuil (0.5)
     */
    public boolean isAddressValid(String address) {
        try {
            // Construire l'URI en remplaçant {q} ou en ajoutant q param
            URI uri;
            if (urlTemplate.contains("{q}")) {
                // simple template replacement
                uri = URI.create(urlTemplate.replace("{q}", UriComponentsBuilder.fromUriString("").build().encode().toUriString()));
                // utiliser getForObject avec param
                Object response = restTemplate.getForObject(urlTemplate, Object.class, address);
                return analyzeResponse(response);
            } else {
                // si urlTemplate est une base sans {q}, on concatene q param
                uri = UriComponentsBuilder.fromHttpUrl(urlTemplate)
                        .queryParam("q", address)
                        .queryParam("limit", 5)
                        .queryParam("autocomplete", 0)
                        .build()
                        .encode()
                        .toUri();
                Object response = restTemplate.getForObject(uri, Object.class);
                return analyzeResponse(response);
            }
        } catch (RestClientException ex) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private boolean analyzeResponse(Object response) {
        if (response == null) return false;

        List<?> features = null;
        if (response instanceof List) {
            features = (List<?>) response;
        } else if (response instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) response;
            Object f = map.get("features");
            if (f instanceof List) features = (List<?>) f;
            else {
                // certains endpoints renvoient la liste directement sous une clef differente
                // try to find a list value
                for (Object v : map.values()) {
                    if (v instanceof List) {
                        features = (List<?>) v;
                        break;
                    }
                }
            }
        }

        if (features == null || features.isEmpty()) return false;

        // Stream pour trouver le score max
        double maxScore = features.stream()
                .filter(obj -> obj instanceof Map)
                .map(obj -> (Map<String, Object>) obj)
                .map(map -> map.get("properties"))
                .filter(prop -> prop instanceof Map)
                .map(prop -> (Map<String, Object>) prop)
                .map(pmap -> pmap.get("score"))
                .filter(scoreObj -> scoreObj != null)
                .mapToDouble(scoreObj -> {
                    if (scoreObj instanceof Number) return ((Number) scoreObj).doubleValue();
                    try { return Double.parseDouble(scoreObj.toString()); } catch (Exception e) { return 0.0; }
                })
                .max()
                .orElse(0.0);

        return maxScore > 0.5;
    }
}
