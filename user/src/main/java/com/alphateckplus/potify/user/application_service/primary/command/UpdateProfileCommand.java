package com.alphateckplus.potify.user.application_service.primary.command;

/**
 * Commande immuable pour transporter les donnees de mise a jour du profil.
 * Respecte SRP (Single Responsibility Principle) en isolant les donnees de la logique.
 * 
 * @param userId L'identifiant de l'utilisateur a modifier
 * @param fullName Le nouveau nom complet
 * @param email Le nouvel email
 */
public record UpdateProfileCommand(
    String userId,
    String fullName
) {}
