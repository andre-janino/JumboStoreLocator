package com.jumbo.userservice.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Class responsible for configuring AMQP RPC communication.
 * 
 * Currently used to provide user information to the auth-service as an alternative to REST HTTP calls.
 * 
 * In the future, consider moving to a CQRS pattern to completely avoid having microservices communicate with one another to query data.
 * In this particular example, auth-service would subscribe to changes in the user-service (create/update/delete) and manage its own copy of the data.
 * 
 * @author André Janino
 */
@Configuration
public class UserConfig {
	
	@Bean
    public Queue queue() {
        return new Queue("user.rpc.requests");
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("user.rpc");
    }

    @Bean
    public Binding binding(DirectExchange exchange, Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with("rpc");
    }
}
