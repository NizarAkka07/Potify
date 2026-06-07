package com.alphateckplus.potify.pool.infrastructure.primary.pool.ai_generate;

import com.alphateckplus.potify.pool.application_service.primary.pool.generate_pool_structure.GeneratePoolStructureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pools/ai")
@RequiredArgsConstructor
@Tag(name = "Pool AI Management")
public class AiPoolController {

    private final GeneratePoolStructureService generatePoolStructureService;

    @PostMapping("/generate")
    @Operation(summary = "Générer la structure d'une cagnotte par IA")
    public ResponseEntity<Map<String, Object>> generatePoolStructure(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        if (prompt == null || prompt.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Le paramètre 'prompt' est obligatoire"));
        }
        try {
            Map<String, Object> suggestion = generatePoolStructureService.execute(prompt);
            return ResponseEntity.ok(suggestion);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
