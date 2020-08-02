package com.jumbo.authservice.configuration;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Class responsible for configuring AMQP RPC communication.
 * 
 * Currently used to fetch user login info from user-service as an alternative to REST HTTP calls.
 * 
 * In the future, consider moving to a CQRS pattern to completely avoid having microservices communicate with one another to query data.
 * In this particular example, auth-service would subscribe to changes in the user-service (create/update/delete) and manage its own copy of the data.
 * 
 * @author André Janino
 */
@Configuration
public class AuthConfig {

	@Bean
	public DirectExchange directExchange() {
		return new DirectExchange("user.rpc");
	}
}