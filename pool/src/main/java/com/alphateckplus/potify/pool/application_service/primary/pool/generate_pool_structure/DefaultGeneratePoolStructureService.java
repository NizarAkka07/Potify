package com.alphateckplus.potify.pool.application_service.primary.pool.generate_pool_structure;

import com.alphateckplus.potify.pool.application_service.secondary.pool.PoolGenerationGatewayPort;
import java.util.Map;

/**
 * Implémentation du cas d'usage de génération de cagnotte par IA.
 */
public class DefaultGeneratePoolStructureService implements GeneratePoolStructureService {

    private final PoolGenerationGatewayPort gatewayPort;

    public DefaultGeneratePoolStructureService(PoolGenerationGatewayPort gatewayPort) {
        this.gatewayPort = gatewayPort;
    }

    @Override
    public Map<String, Object> execute(String prompt) {
        return gatewayPort.generateStructure(prompt);
    }
}
