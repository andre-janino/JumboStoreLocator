package com.jumbo.authservice.security;

import java.io.IOException;
import java.util.List;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumbo.authservice.security.JwtUsernameAndPasswordAuthenticationFilter.UserCredentials;

/**
 * If the provided user is present on the database, create a Spring user object with the proper authorities
 * 
 * @author André Janino
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService  {

	// TODO: remove this hardcoded admin role handle it on the user-service.
	private static final String ROLE_ADMIN = "ROLE_ADMIN";

	private RabbitTemplate rabbitTemplate;
	private DirectExchange directExchange;
	
	/**
	 * Initialize the properties needed for RabbitMQ 
	 * @param environment
	 * @param rabbitTemplate
	 * @param directExchange
	 */
	@Autowired	
    public UserDetailsServiceImpl(RabbitTemplate rabbitTemplate, DirectExchange directExchange) {	
        this.rabbitTemplate = rabbitTemplate;	
        this.directExchange = directExchange;	
    }
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// retrieve a representation of the user object from user-service
		UserCredentials user = this.getUserInfoMessageRpc(username);
		if(user != null) {
			// as roles are not yet implemented, issue an ADMIN role to our user
			List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(ROLE_ADMIN);
			
			// returns a Spring user, which is employed by UserDetailsService to manage the authentication
			return new User(username, user.getPassword(), grantedAuthorities);
		}
		
		// if this point is reached, user was not found; throw an exception
		throw new UsernameNotFoundException("Username: " + username + " not found");
	}
	
	/**
	 * Fire an RPC message to user-service to fetch user info by email.
	 * 
	 * @param email
	 * @return the user info, if any.
	 */
	public UserCredentials getUserInfoMessageRpc(String email) {
		// get a user json object from user-service
	    String user = (String) rabbitTemplate.convertSendAndReceive(directExchange.getName(), "rpc", email);

	    // deserialize into UserCredentials and return
	    return deserializeToUserCredentials(user);
	}

	/**
	 * Deserialize a user json object into the UserCredentials class
	 * 
	 * @param user json object
	 * @return a UserCredentials object
	 */
	private UserCredentials deserializeToUserCredentials(String user) {
		TypeReference<UserCredentials> mapType = new TypeReference<UserCredentials>() {};
	    ObjectMapper objectMapper = new ObjectMapper();
	    try {
	    	return objectMapper.readValue(user, mapType);
	    } catch (IOException e) {
	        System.out.println(String.valueOf(e));
	    }
	    return null;
	}
}