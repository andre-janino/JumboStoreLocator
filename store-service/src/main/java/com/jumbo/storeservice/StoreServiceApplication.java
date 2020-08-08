package com.jumbo.storeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * The core of this project, a microservice responsible for fetching filtered store data based on user input.
 * 
 * @author André Janino
 */
@SpringBootApplication
@EnableEurekaClient
public class StoreServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StoreServiceApplication.class, args);
	}
}
