package com.jumbo.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;

/**
 * Authentication service, responsible for validating and issuing JWT tokens. 
 * 
 * @author André Janino
 */
@SpringBootApplication
@EnableCircuitBreaker
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}
}
