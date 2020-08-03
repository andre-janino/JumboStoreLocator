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
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

/**
 * If the provided user is present on the database, create a Spring user object with the proper authorities
 * 
 * @author André Janino
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService  {

	private static final String ROLE_PREFIX = "ROLE_";
	private static final String GUEST_USER = "Guest";
	private static final String ROLE_READ_ONLY = "READONLY";

	private RabbitTemplate rabbitTemplate;
	private DirectExchange directExchange;
	
	private static UserCredentials guestUser;
    private static UserCredentials getGuestUser() {
        if (guestUser == null){ 
        	guestUser = new UserCredentials(GUEST_USER, GUEST_USER, "$2a$10$heirHA89ULwxENiWxaj25O1S9oRafpyvLQw21shSNWhV7i/VuZNJ6", ROLE_READ_ONLY);
        }
        return guestUser;
    }
	
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
	 * Load a user from user-service
	 * 
	 * @param username The username key of the user credentials. In this case, the user email.
	 */
	@Override
	@HystrixCommand(fallbackMethod = "loadGuestUser")
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// retrieve a representation of the user object from user-service
		UserCredentials user = this.getUserInfoMessageRpc(username);
		
		// define the user role
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(getRole(user));
		
		// returns a Spring user, which is employed by UserDetailsService to manage the authentication
		return new User(username, user.getPassword(), grantedAuthorities);
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
	    if(user == null) throw new UsernameNotFoundException("Username: " + email + " not found");
	    
	    // deserialize into UserCredentials and return
	    return deserializeToUserCredentials(user);
	}
	
	/**
	 * Fallback method in case the RPC communication channel fails.
	 * 
	 * @param email The original requested email.
	 * @param hystrixCommand The hytrix exception that was captured.
	 * @return A UserDetails object based on  a guest user instance.
	 */
	public UserDetails loadGuestUser(String username, Throwable hystrixCommand) {
		UserCredentials user = getGuestUser();
		
		// grant read_only capabilities so that the user is able to access the app with limited functionalities
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRole());
		
		// return the guest user
		return new User(user.getEmail(), user.getPassword(), grantedAuthorities);
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