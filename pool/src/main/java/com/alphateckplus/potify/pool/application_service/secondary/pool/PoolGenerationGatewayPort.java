package com.alphateckplus.potify.pool.application_service.secondary.pool;

import java.util.Map;

/**
 * Port de sortie secondaire pour la génération de structure de cagnotte par IA.
 */
public interface PoolGenerationGatewayPort {
    Map<String, Object> generateStructure(String prompt);
}
