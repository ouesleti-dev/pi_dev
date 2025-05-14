package com.example.javaproject.Services;

import java.net.*;
import java.net.http.*;
import java.time.Duration;
import java.nio.charset.StandardCharsets;

public class BadWordsAPIService {
    private static final String API_URL = "https://www.purgomalum.com/service/containsprofanity?text=";
    private final HttpClient httpClient;

    public BadWordsAPIService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
    }

    public boolean containsProfanity(String text) throws Exception {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }

        try {
            // Correction: Utilisation de URLEncoder au lieu de URI.encode()
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8.toString());
            String fullUrl = API_URL + encodedText;

            // Validation de l'URL
            URI uri = new URI(fullUrl);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .timeout(Duration.ofSeconds(3))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Erreur HTTP: " + response.statusCode());
            }

            return Boolean.parseBoolean(response.body());
        } catch (Exception e) {
            System.err.println("Erreur API BadWords: " + e.getMessage());
            throw new Exception("Service de vérification indisponible", e);
        }
    }
}