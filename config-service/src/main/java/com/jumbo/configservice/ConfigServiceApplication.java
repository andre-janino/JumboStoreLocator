package com.jumbo.configservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Config service application that stores the properties of other Jumbo store locator's microservices.
 * 
 * Running with JDBC back-end. As other SQL implementations in this project, currently setup to use H2 in-memory database.
 * 
 * @author André Janino
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigServiceApplication.class, args);
	}
}