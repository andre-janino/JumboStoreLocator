package com.jumbo.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Spring Boot main class for a user microservice
 * 
 * @author André Janino
 */
@SpringBootApplication
@EnableEurekaClient
public class UserServiceApplication {

	public static void main(String[] args) {
		
		SpringApplication.run(UserServiceApplication.class, args);
	}
}
