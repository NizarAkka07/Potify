package com.alphateckplus.potify.support.infrastructure.secondary.ai;

import com.alphateckplus.potify.support.application_service.secondary.SupportAiEnginePort;
import java.util.List;
import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class PotifyAiSupportEngine implements SupportAiEnginePort {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AiSupportResponse {
        private String replyText;
        private boolean shouldEscalate;
        private String detectedCategory;
    }

    private static final List<String> HUMAN_REQUEST_KEYWORDS = List.of(
            "humain", "administrateur", "admin", "agent", "conseiller",
            "parler à quelqu'un", "support humain", "personne", "probleme grave",
            "contacter un admin", "escalader", "reclamation", "fraude"
    );

    @Override
    public AiSupportResponse generateReply(String userMessageContent) {
        String normalizedMsg = userMessageContent.toLowerCase(Locale.ROOT).trim();

        boolean requestsHuman = HUMAN_REQUEST_KEYWORDS.stream().anyMatch(normalizedMsg::contains);
        if (requestsHuman) {
            return AiSupportResponse.builder()
                    .replyText("J'ai bien pris en compte votre demande. Je transfère immédiatement cette conversation à un administrateur du support Potify. Un conseiller va vous répondre sous peu.")
                    .shouldEscalate(true)
                    .detectedCategory("Escalade Humaine")
                    .build();
        }

        if (normalizedMsg.contains("cagnotte") && (normalizedMsg.contains("créer") || normalizedMsg.contains("comment"))) {
            return AiSupportResponse.builder()
                    .replyText("Pour créer une cagnotte sur Potify :\n1. Connectez-vous à votre compte.\n2. Cliquez sur le bouton '+ Créer une cagnotte'.\n3. Renseignez le titre, l'objectif financier et la catégorie.\n4. Soumettez votre cagnotte pour publication !")
                    .shouldEscalate(false)
                    .detectedCategory("Gestion Cagnottes")
                    .build();
        }

        if (normalizedMsg.contains("frais") || normalizedMsg.contains("commission") || normalizedMsg.contains("pourcentage")) {
            return AiSupportResponse.builder()
                    .replyText("Sur Potify, la commission standard plateforme est de 2.00% sur les collectes. Aucun frais caché ne s'lique aux donateurs.")
                    .shouldEscalate(false)
                    .detectedCategory("Frais & Paiements")
                    .build();
        }

        if (normalizedMsg.contains("retrait") || normalizedMsg.contains("virement") || normalizedMsg.contains("iban") || normalizedMsg.contains("recuperer argent")) {
            return AiSupportResponse.builder()
                    .replyText("Les retraits de fonds s'effectuent depuis votre Wallet Potify vers votre compte bancaire (IBAN). Les demandes sont traitées sous 24 à 48 heures ouvrées après vérification de votre identité (KYC).")
                    .shouldEscalate(false)
                    .detectedCategory("Retraits & IBAN")
                    .build();
        }

        if (normalizedMsg.contains("kyc") || normalizedMsg.contains("vérification") || normalizedMsg.contains("identite") || normalizedMsg.contains("piece d'identite")) {
            return AiSupportResponse.builder()
                    .replyText("La vérification KYC est obligatoire avant d'effectuer un retrait. Vous devez fournir une pièce d'identité valide (CNI ou Passeport) et un justificatif de domicile depuis les paramètres de votre profil.")
                    .shouldEscalate(false)
                    .detectedCategory("Vérification KYC")
                    .build();
        }

        if (normalizedMsg.contains("don") || normalizedMsg.contains("payer") || normalizedMsg.contains("carte") || normalizedMsg.contains("paypal") || normalizedMsg.contains("stripe")) {
            return AiSupportResponse.builder()
                    .replyText("Vous pouvez contribuer à une cagnotte de manière sécurisée par carte bancaire (Stripe) ou via PayPal. Vos paiements sont 100% cryptés et protégés.")
                    .shouldEscalate(false)
                    .detectedCategory("Dons & Paiements")
                    .build();
        }

        if (normalizedMsg.contains("mot de passe") || normalizedMsg.contains("compte") || normalizedMsg.contains("securite") || normalizedMsg.contains("connexion")) {
            return AiSupportResponse.builder()
                    .replyText("Pour des raisons de sécurité, si vous rencontrez un problème d'accès à votre compte ou souhaitez modifier votre mot de passe, rendez-vous sur la page d'accueil et cliquez sur 'Mot de passe oublié'.")
                    .shouldEscalate(false)
                    .detectedCategory("Compte & Sécurité")
                    .build();
        }

        return AiSupportResponse.builder()
                .replyText("Je suis le Chatbot intelligent Potify. Je peux vous aider sur la création de cagnottes, les dons, les retraits (KYC), les frais ou les comptes.\n\nSi vous souhaitez parler directement à un membre de notre équipe d'assistance, dites-moi simplement 'Je veux parler à un humain' ou cliquez sur le bouton d'escalade.")
                .shouldEscalate(false)
                .detectedCategory("Assistance Générale")
                .build();
    }
}
