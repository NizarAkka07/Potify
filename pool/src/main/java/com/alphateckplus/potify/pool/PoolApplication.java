package com.alphateckplus.potify.pool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.alphateckplus.potify.data_jpa.repository")
@EntityScan(basePackages = "com.alphateckplus.potify.data_jpa.entity")
public class PoolApplication {

	public static void main(String[] args) {
		SpringApplication.run(PoolApplication.class, args);
	}

	@org.springframework.context.annotation.Bean
	public org.springframework.boot.CommandLineRunner dropConstraint(org.springframework.jdbc.core.JdbcTemplate jdbcTemplate) {
		return args -> {
			try {
				System.out.println(">>> [Nettoyage] Suppression de la contrainte pools_status_check...");
				jdbcTemplate.execute("ALTER TABLE pools DROP CONSTRAINT IF EXISTS pools_status_check");
				System.out.println(">>> [Nettoyage] Contrainte supprimée avec succès.");
			} catch (Exception e) {
				System.out.println(">>> [Nettoyage] Erreur lors de la suppression (peut-être déjà supprimée) : " + e.getMessage());
			}
		};
	}

}
