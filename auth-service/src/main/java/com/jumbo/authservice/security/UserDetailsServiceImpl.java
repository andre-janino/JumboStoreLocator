package com.jumbo.authservice.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumbo.authservice.security.JwtUsernameAndPasswordAuthenticationFilter.UserCredentials;

/**
 * If the provided user is present on the database, create a Spring user object with the proper authorities
 * 
 * @author André Janino
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService  {

	private static final String ROLE_PREFIX = "ROLE_";	

	private RabbitTemplate rabbitTemplate;
	private DirectExchange directExchange;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
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
	
	/**
	 * Load a user from user-service.
	 * 
	 * @param username The username key of the user credentials. In this case, the user email.
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// retrieve a representation of the user object from user-service. 
		log.info("Attempting to load user: " + username);
		UserCredentials user = this.getUserInfoMessageRpc(username);

		// define the user role
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(getRole(user));
		
		// returns a Spring user, which is employed by UserDetailsService to manage the authentication
		return buildAuthUser(username, user, grantedAuthorities);
	}

	/**
	 * Build a custom auth user to hold additional information.
	 * 
	 * @param username The requested username. 
	 * @param user The temp user credentials object, deserialized from user-service RPC response
	 * @param grantedAuthorities The granted authorities (ADMIN, USER or GUEST)
	 * @return A custom authenticated user object
	 */
	private UserDetails buildAuthUser(String username, UserCredentials user, List<GrantedAuthority> grantedAuthorities) {
		// build the default object and fill in the remainder of the info
		AuthenticatedUser authUser = new AuthenticatedUser(username, user.getPassword(), grantedAuthorities);
		authUser.setFirstName(user.getFirstName());
		authUser.setLastName(user.getLastName());
		authUser.setRole(user.getRole());
		authUser.setEmail(user.getEmail());
		
		log.info("UserDetails object built for " + user.getEmail());
		return authUser;
	}
	
	/**
	 * Fire an RPC message to user-service to fetch user info by email.
	 * 
	 * @param email
	 * @return the user info, if any.
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	public UserCredentials getUserInfoMessageRpc(String username) {
		// get a user json object from user-service
		log.info("Requesting user information through user.rpc call for: " + username);
	    String user = (String) rabbitTemplate.convertSendAndReceive(directExchange.getName(), "rpc", username);

	    // deserialize into UserCredentials and return
	    log.info("Received json user properties: " + user);
	    return deserializeToUserCredentials(user);
	}

	/**
	 * Deserialize a user json object into the UserCredentials class
	 * 
	 * @param user json object
	 * @return a UserCredentials object
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	private UserCredentials deserializeToUserCredentials(String user) {
		log.info("Deserializing user json into UserCredentials object");
		TypeReference<UserCredentials> mapType = new TypeReference<UserCredentials>() {};
	    ObjectMapper objectMapper = new ObjectMapper();
	    try {
			return objectMapper.readValue(user, mapType);
		} catch (Exception e) {
			log.error("There was a problem deserializing the provided user json.", e);
		} 
	    return null;
	}
	
	/**
	 * Append ROLE_ prefix to the user role name.
	 * 
	 * @param user The user object which holds the role name
	 * @return An authority compliant role property
	 */
	private String getRole(UserCredentials user) {
		return new StringBuilder(ROLE_PREFIX).append(user.getRole()).toString();
	}
}