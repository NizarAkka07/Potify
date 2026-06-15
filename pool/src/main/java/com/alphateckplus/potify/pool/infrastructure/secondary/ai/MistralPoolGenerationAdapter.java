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
 * Adaptateur secondaire pour la génération de cagnotte via l'API Mistral AI.
 */
@Slf4j
public class MistralPoolGenerationAdapter implements PoolGenerationGatewayPort {

    @Value("${application.ai.mistral.api-key:}")
    private String mistralApiKeyFromConfig;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> generateStructure(String prompt) {
        String apiKey = System.getenv("MISTRAL_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = mistralApiKeyFromConfig;
        }

        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException("La clé API Mistral (MISTRAL_API_KEY) n'est pas configurée.");
        }

        try {
            String url = "https://api.mistral.ai/v1/chat/completions";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

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

            // Define request payload for Mistral AI
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "mistral-large-latest");
            
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", instructions);
            requestBody.put("messages", List.of(message));

            // Structured JSON output configuration
            Map<String, Object> responseSchemaProperties = new HashMap<>();
            responseSchemaProperties.put("title", Map.of("type", "string"));
            responseSchemaProperties.put("description", Map.of("type", "string"));
            responseSchemaProperties.put("category", Map.of("type", "string", "enum", List.of("Santé", "Éducation", "Urgence", "Animaux", "Projets", "Sport")));
            responseSchemaProperties.put("goalAmount", Map.of("type", "number"));
            responseSchemaProperties.put("mode", Map.of("type", "string", "enum", List.of("simple", "multi")));
            
            // Phase schema
            Map<String, Object> phaseProperties = new HashMap<>();
            phaseProperties.put("title", Map.of("type", "string"));
            phaseProperties.put("goalAmount", Map.of("type", "number"));
            Map<String, Object> phaseSchema = Map.of(
                "type", "object",
                "properties", phaseProperties,
                "required", List.of("title", "goalAmount"),
                "additionalProperties", false
            );

            // Simple phases schema
            responseSchemaProperties.put("simplePhases", Map.of(
                "type", "array",
                "items", phaseSchema
            ));

            // Subpool schema
            Map<String, Object> subPoolProperties = new HashMap<>();
            subPoolProperties.put("title", Map.of("type", "string"));
            subPoolProperties.put("description", Map.of("type", "string"));
            subPoolProperties.put("hasDeadline", Map.of("type", "boolean"));
            subPoolProperties.put("phases", Map.of(
                "type", "array",
                "items", phaseSchema
            ));
            Map<String, Object> subPoolSchema = Map.of(
                "type", "object",
                "properties", subPoolProperties,
                "required", List.of("title", "phases"),
                "additionalProperties", false
            );

            responseSchemaProperties.put("subPools", Map.of(
                "type", "array",
                "items", subPoolSchema
            ));

            Map<String, Object> responseSchema = Map.of(
                "type", "object",
                "properties", responseSchemaProperties,
                "required", List.of("title", "description", "category", "goalAmount", "mode"),
                "additionalProperties", false
            );

            Map<String, Object> jsonSchemaConfig = Map.of(
                "name", "pool_structure_schema",
                "strict", true,
                "schema", responseSchema
            );

            Map<String, Object> responseFormat = Map.of(
                "type", "json_schema",
                "json_schema", jsonSchemaConfig
            );

            requestBody.put("response_format", responseFormat);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                Map<String, Object> responseMap = objectMapper.readValue(responseEntity.getBody(), Map.class);
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, Object> messageMap = (Map<String, Object>) choice.get("message");
                    if (messageMap != null) {
                        String responseJson = (String) messageMap.get("content");
                        log.info("Réponse brute de l'IA (Mistral) : {}", responseJson);
                        return objectMapper.readValue(responseJson, Map.class);
                    }
                }
            }
            throw new RuntimeException("La requête Mistral API a échoué avec le statut : " + responseEntity.getStatusCode());
        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            log.error("Erreur HTTP Mistral API : Status = {}, Body = {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Erreur Mistral API (" + e.getStatusCode() + ") : " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Erreur lors de l'appel Mistral API : {}", e.getMessage(), e);
            throw new RuntimeException("Erreur de communication avec Mistral : " + e.getMessage(), e);
        }
    }
}
