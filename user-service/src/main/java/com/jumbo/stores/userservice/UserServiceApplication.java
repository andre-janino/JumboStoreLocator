package com.jumbo.stores.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Spring Boot main class for a user microservice
 * 
 * @author André Janino
 */
@EnableJpaRepositories(basePackages = "com.jumbo.stores.userservice.repository")
@SpringBootApplication
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}
}
