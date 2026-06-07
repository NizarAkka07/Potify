package com.alphateckplus.potify.pool.infrastructure.secondary.ai;

import com.alphateckplus.potify.pool.application_service.secondary.pool.PoolGenerationGatewayPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Adaptateur secondaire pour la génération de cagnotte via l'API Gemini.
 */
@Slf4j
public class GeminiPoolGenerationAdapter implements PoolGenerationGatewayPort {

    @Value("${application.ai.gemini.api-key:}")
    private String geminiApiKeyFromConfig;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> generateStructure(String prompt) {
        String apiKey = System.getenv("GEMINI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = geminiApiKeyFromConfig;
        }

        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException("La clé API Gemini (GEMINI_API_KEY) n'est pas configurée.");
        }

        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String instructions = "Tu es un assistant IA spécialisé dans la structuration de cagnottes (crowdfunding) pour la plateforme Potify.\n" +
                    "Prends le besoin de l'utilisateur et génère une structure de cagnotte en français.\n" +
                    "Tu DOIS retourner STRICTEMENT un objet JSON respectant le format décrit par ce schéma sans aucun autre texte :\n" +
                    "{\n" +
                    "  \"title\": \"Titre court et accrocheur\",\n" +
                    "  \"description\": \"Description détaillée du projet (2-3 paragraphes)\",\n" +
                    "  \"category\": \"Santé ou Éducation ou Urgence ou Animaux ou Projets ou Sport\",\n" +
                    "  \"goalAmount\": 5000,\n" +
                    "  \"mode\": \"simple ou multi\",\n" +
                    "  \"simplePhases\": [\n" +
                    "    { \"title\": \"Nom de la phase 1\", \"goalAmount\": 2500 },\n" +
                    "    { \"title\": \"Nom de la phase 2\", \"goalAmount\": 2500 }\n" +
                    "  ],\n" +
                    "  \"subPools\": [\n" +
                    "    {\n" +
                    "      \"title\": \"Titre de la sous-cagnotte\",\n" +
                    "      \"description\": \"Description\",\n" +
                    "      \"hasDeadline\": false,\n" +
                    "      \"phases\": [\n" +
                    "        { \"title\": \"Étape 1\", \"goalAmount\": 2500 }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}\n\n" +
                    "Règles importantes :\n" +
                    "1. La catégorie doit être EXACTEMENT l'une de ces valeurs : \"Santé\", \"Éducation\", \"Urgence\", \"Animaux\", \"Projets\", \"Sport\".\n" +
                    "2. Le 'mode' doit être 'simple' si le projet est séquentiel (phases de progression simples) ou 'multi' si le projet doit être divisé en plusieurs sous-cagnottes distinctes.\n" +
                    "3. Si mode = 'simple', remplis 'simplePhases' (au moins une phase) et laisse 'subPools' vide.\n" +
                    "4. Si mode = 'multi', remplis 'subPools' (au moins une sous-cagnotte, contenant chacune au moins une phase) et laisse 'simplePhases' vide.\n" +
                    "5. La somme des budgets des phases (simple ou dans les sous-cagnottes) doit égaler exactement 'goalAmount'.\n" +
                    "Le besoin de l'utilisateur est : \"" + prompt + "\"";

            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", instructions);

            Map<String, Object> partContainer = new HashMap<>();
            partContainer.put("parts", List.of(textPart));

            Map<String, Object> contents = new HashMap<>();
            contents.put("contents", List.of(partContainer));

            // Imposer un type de réponse JSON structuré
            Map<String, Object> responseSchemaProperties = new HashMap<>();
            responseSchemaProperties.put("title", Map.of("type", "STRING"));
            responseSchemaProperties.put("description", Map.of("type", "STRING"));
            responseSchemaProperties.put("category", Map.of("type", "STRING", "enum", List.of("Santé", "Éducation", "Urgence", "Animaux", "Projets", "Sport")));
            responseSchemaProperties.put("goalAmount", Map.of("type", "NUMBER"));
            responseSchemaProperties.put("mode", Map.of("type", "STRING", "enum", List.of("simple", "multi")));
            
            // Phase schema
            Map<String, Object> phaseProperties = new HashMap<>();
            phaseProperties.put("title", Map.of("type", "STRING"));
            phaseProperties.put("goalAmount", Map.of("type", "NUMBER"));
            Map<String, Object> phaseSchema = Map.of(
                "type", "OBJECT",
                "properties", phaseProperties,
                "required", List.of("title", "goalAmount")
            );

            // Simple phases schema
            responseSchemaProperties.put("simplePhases", Map.of(
                "type", "ARRAY",
                "items", phaseSchema
            ));

            // Subpool schema
            Map<String, Object> subPoolProperties = new HashMap<>();
            subPoolProperties.put("title", Map.of("type", "STRING"));
            subPoolProperties.put("description", Map.of("type", "STRING"));
            subPoolProperties.put("hasDeadline", Map.of("type", "BOOLEAN"));
            subPoolProperties.put("phases", Map.of(
                "type", "ARRAY",
                "items", phaseSchema
            ));
            Map<String, Object> subPoolSchema = Map.of(
                "type", "OBJECT",
                "properties", subPoolProperties,
                "required", List.of("title", "phases")
            );

            responseSchemaProperties.put("subPools", Map.of(
                "type", "ARRAY",
                "items", subPoolSchema
            ));

            Map<String, Object> responseSchema = Map.of(
                "type", "OBJECT",
                "properties", responseSchemaProperties,
                "required", List.of("title", "description", "category", "goalAmount", "mode")
            );

            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("responseMimeType", "application/json");
            generationConfig.put("responseSchema", responseSchema);

            contents.put("generationConfig", generationConfig);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(contents, headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                Map<String, Object> responseMap = objectMapper.readValue(responseEntity.getBody(), Map.class);
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseMap.get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> contentMap = (Map<String, Object>) candidates.get(0).get("content");
                    if (contentMap != null) {
                        List<Map<String, Object>> parts = (List<Map<String, Object>>) contentMap.get("parts");
                        if (parts != null && !parts.isEmpty()) {
                            String responseJson = (String) parts.get(0).get("text");
                            log.info("Réponse brute de l'IA : {}", responseJson);
                            return objectMapper.readValue(responseJson, Map.class);
                        }
                    }
                }
            }
            throw new RuntimeException("La requête Gemini API a échoué avec le statut : " + responseEntity.getStatusCode());
        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            log.error("Erreur HTTP Gemini API : Status = {}, Body = {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Erreur Gemini API (" + e.getStatusCode() + ") : " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Erreur lors de l'appel Gemini API : {}", e.getMessage(), e);
            throw new RuntimeException("Erreur de communication avec Gemini : " + e.getMessage(), e);
        }
    }
}
