package com.alphateckplus.potify.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@org.springframework.context.annotation.ComponentScan(basePackages = "com.alphateckplus.potify.payment")
@EnableJpaRepositories(basePackages = "com.alphateckplus.potify.data_jpa.repository")
@EntityScan(basePackages = "com.alphateckplus.potify.data_jpa.entity")
@org.springframework.data.jpa.repository.config.EnableJpaAuditing
public class PaymentApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentApplication.class, args);
	}
}
