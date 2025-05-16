package Services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

public class AIService {
    private static final String API_KEY = "AIzaSyBuU0jiCTEI1Pg54rS9O2N6k0g1sWL-W6E";
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    public static String generateResponse(String message) throws Exception {
        // Construction du payload optimisé pour le français
        JSONObject payload = new JSONObject()
                .put("contents", new JSONArray()
                        .put(new JSONObject()
                                .put("parts", new JSONArray()
                                        .put(new JSONObject()
                                                .put("text",
                                                        "Rôle: Assistant clientèle professionnel\n" +
                                                                "Langue: Français\n" +
                                                                "Style: Courtois et concis\n" +
                                                                "Longueur: 2-3 phrases maximum\n\n" +
                                                                "Réponds à cette réclamation:\n" +
                                                                message)
                                        )
                                )
                        )
                )
                .put("generationConfig", new JSONObject()
                        .put("maxOutputTokens", 200)
                        .put("temperature", 0.5));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(GEMINI_URL + "?key=" + API_KEY))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        // Traitement de la réponse avec vérification d'erreur
        if (response.statusCode() == 200) {
            JSONObject jsonResponse = new JSONObject(response.body());
            return extractGeminiResponse(jsonResponse);
        } else {
            throw new RuntimeException(formatGeminiError(response));
        }
    }

    private static String extractGeminiResponse(JSONObject jsonResponse) {
        try {
            return jsonResponse.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");
        } catch (Exception e) {
            throw new RuntimeException("Format de réponse Gemini invalide: " + jsonResponse.toString());
        }
    }

    private static String formatGeminiError(HttpResponse<String> response) {
        try {
            JSONObject errorJson = new JSONObject(response.body());
            return errorJson.getJSONObject("error").getString("message");
        } catch (Exception e) {
            return "Erreur Gemini (" + response.statusCode() + "): " + response.body();
        }
    }
}