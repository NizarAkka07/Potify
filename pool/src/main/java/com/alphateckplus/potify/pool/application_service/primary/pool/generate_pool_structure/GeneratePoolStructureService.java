package com.alphateckplus.potify.pool.application_service.primary.pool.generate_pool_structure;

import java.util.Map;

/**
 * Port d'entrée primaire pour la génération de structure de cagnotte par IA.
 */
public interface GeneratePoolStructureService {
    Map<String, Object> execute(String prompt);
}
