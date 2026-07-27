package com.alphateckplus.potify.support.infrastructure.secondary.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.alphateckplus.potify.support.infrastructure.secondary.ai.PotifyAiSupportEngine.AiSupportResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PotifyAiSupportEngineTest {

    private PotifyAiSupportEngine aiSupportEngine;

    @BeforeEach
    void setUp() {
        aiSupportEngine = new PotifyAiSupportEngine();
    }

    @Test
    @DisplayName("Devrait détecter la demande d'escalade vers un humain")
    void testHumanEscalationKeyword() {
        AiSupportResponse response = aiSupportEngine.generateReply("Je veux parler à un administrateur s'il vous plaît");

        assertNotNull(response);
        assertTrue(response.isShouldEscalate(), "L'escalade vers un humain doit être activée");
        assertEquals("Escalade Humaine", response.getDetectedCategory());
        assertTrue(response.getReplyText().contains("administrateur"));
    }

    @Test
    @DisplayName("Devrait répondre aux questions sur la création de cagnotte")
    void testPoolCreationQuestion() {
        AiSupportResponse response = aiSupportEngine.generateReply("Comment créer une cagnotte ?");

        assertNotNull(response);
        assertFalse(response.isShouldEscalate());
        assertEquals("Gestion Cagnottes", response.getDetectedCategory());
        assertTrue(response.getReplyText().contains("Créer une cagnotte"));
    }

    @Test
    @DisplayName("Devrait répondre aux questions sur les frais et commission")
    void testFeesQuestion() {
        AiSupportResponse response = aiSupportEngine.generateReply("Quels sont les frais de commission ?");

        assertNotNull(response);
        assertFalse(response.isShouldEscalate());
        assertEquals("Frais & Paiements", response.getDetectedCategory());
        assertTrue(response.getReplyText().contains("2.00%"));
    }

    @Test
    @DisplayName("Devrait répondre aux questions sur la vérification KYC")
    void testKycQuestion() {
        AiSupportResponse response = aiSupportEngine.generateReply("Comment faire la vérification identite kyc ?");

        assertNotNull(response);
        assertFalse(response.isShouldEscalate());
        assertEquals("Vérification KYC", response.getDetectedCategory());
        assertTrue(response.getReplyText().contains("CNI"));
    }

    @Test
    @DisplayName("Devrait retourner une réponse générale de secours si la question n'est pas répertoriée")
    void testGenericFallback() {
        AiSupportResponse response = aiSupportEngine.generateReply("Bonjour, il fait beau");

        assertNotNull(response);
        assertFalse(response.isShouldEscalate());
        assertEquals("Assistance Générale", response.getDetectedCategory());
        assertTrue(response.getReplyText().contains("Chatbot intelligent Potify"));
    }
}
