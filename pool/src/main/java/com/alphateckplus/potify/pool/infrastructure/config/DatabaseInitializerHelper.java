package com.alphateckplus.potify.pool.infrastructure.config;

import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class DatabaseInitializerHelper {

    public DatabaseInitializerHelper(DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            // S'assurer que les changements sont persistés immédiatement
            conn.setAutoCommit(true);
            System.out.println(">>> [Initialisation] Mise à jour des valeurs nulles de la colonne fees dans pools, contributions et transactions...");
            try (Statement stmt = conn.createStatement()) {
                // Table pools
                try {
                    stmt.execute("ALTER TABLE pools ADD COLUMN IF NOT EXISTS fees numeric(19,2)");
                } catch (Exception ex) {
                    System.out.println(">>> [Initialisation] Note: Colonne fees de pools déjà présente.");
                }
                stmt.execute("UPDATE pools SET fees = 2.00 WHERE fees IS NULL");

                // Table contributions
                try {
                    stmt.execute("ALTER TABLE contributions ADD COLUMN IF NOT EXISTS fees numeric(19,2)");
                } catch (Exception ex) {
                    System.out.println(">>> [Initialisation] Note: Colonne fees de contributions déjà présente.");
                }
                stmt.execute("UPDATE contributions SET fees = 0.00 WHERE fees IS NULL");

                // Table transactions
                try {
                    stmt.execute("ALTER TABLE transactions ADD COLUMN IF NOT EXISTS fees numeric(19,2)");
                } catch (Exception ex) {
                    System.out.println(">>> [Initialisation] Note: Colonne fees de transactions déjà présente.");
                }
                stmt.execute("UPDATE transactions SET fees = 0.00 WHERE fees IS NULL");

                System.out.println(">>> [Initialisation] Mise à jour de toutes les colonnes fees effectuée avec succès.");
            }
        } catch (Exception e) {
            System.out.println(">>> [Initialisation] Erreur lors de la mise à jour des frais : " + e.getMessage());
        }
    }
}
