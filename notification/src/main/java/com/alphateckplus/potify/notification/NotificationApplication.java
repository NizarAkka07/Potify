package com.alphateckplus.potify.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.alphateckplus.potify.data_jpa.repository")
@EntityScan(basePackages = "com.alphateckplus.potify.data_jpa.entity")
@org.springframework.data.jpa.repository.config.EnableJpaAuditing
public class NotificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationApplication.class, args);
	}

	@org.springframework.context.annotation.Bean
	public org.springframework.boot.CommandLineRunner dropConstraint(org.springframework.jdbc.core.JdbcTemplate jdbcTemplate) {
		return args -> {
			try {
				System.out.println(">>> [Nettoyage] Suppression de la contrainte notifications_type_check...");
				jdbcTemplate.execute("ALTER TABLE notifications DROP CONSTRAINT IF EXISTS notifications_type_check");
				System.out.println(">>> [Nettoyage] Contrainte notifications_type_check supprimee avec succes.");
			} catch (Exception e) {
				System.out.println(">>> [Nettoyage] Erreur lors de la suppression de la contrainte : " + e.getMessage());
			}
		};
	}

}
