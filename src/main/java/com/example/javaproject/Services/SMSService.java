package com.example.javaproject.Services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class SMSService {
    private static final String API_URL = "https://51zp1j.api.infobip.com/sms/2/text/advanced";
    private static final String API_KEY = "App d1757d33e10c9ba53e1fa09ca0993e29-0fd29afe-7bf1-4d1c-8037-7f02be53960d";
    private static final String SENDER_NUMBER = "447491163443";
    private final HttpClient httpClient;

    public SMSService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public void sendAdminNotification(String adminPhone) throws Exception {
        // JSON le plus simple possible
        String jsonBody = String.format(
                "{\"messages\":[{" +
                        "\"destinations\":[{\"to\":\"%s\"}]," +
                        "\"from\":\"%s\"," +
                        "\"text\":\"Il y a une nouvelle réclamation\"" +
                        "}]}",
                adminPhone,
                SENDER_NUMBER
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(API_URL))
                .header("Authorization", API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .timeout(Duration.ofSeconds(5))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Erreur API: " + response.body());
        }
    }
}