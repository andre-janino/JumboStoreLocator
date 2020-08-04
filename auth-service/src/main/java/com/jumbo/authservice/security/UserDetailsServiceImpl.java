package com.jumbo.authservice.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
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
	
	@Value("${user.guest.name:Guest}")
	private String guestUserName;
	
	@Value("${user.guest.password}")
	private String guestUserPass;
	
	@Value("${user.guest.role:GUEST}")
	private String guestUserRole; 

	private RabbitTemplate rabbitTemplate;
	private DirectExchange directExchange;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Guest user singleton, employed when user-service is down.
	 */
	private static UserCredentials guestUser;
    private static UserCredentials getGuestUser(String guestUserName, String guestUserPass, String guestUserRole) {
        if (guestUser == null){ 
        	guestUser = new UserCredentials(guestUserName, guestUserName, guestUserPass, guestUserRole);
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
	 * Load a user from user-service.
	 * 
	 * @param username The username key of the user credentials. In this case, the user email.
	 */
	@Override
	@HystrixCommand(fallbackMethod = "loadGuestUser")
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			log.info("Attempting to load user " + username);
			
			// retrieve a representation of the user object from user-service
			UserCredentials user = this.getUserInfoMessageRpc(username);
			
			// define the user role
			List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(getRole(user));
			
			// returns a Spring user, which is employed by UserDetailsService to manage the authentication
			log.info("UserDetails object built for " + user.getUsername());
			return new User(user.getUsername(), user.getPassword(), grantedAuthorities);
			
		} catch(Exception e) {
			log.error("There was a problem loading the user.", e);
		} 
		return null;
	}
	
	/**
	 * Fire an RPC message to user-service to fetch user info by email.
	 * 
	 * @param email
	 * @return the user info, if any.
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	public UserCredentials getUserInfoMessageRpc(String email) throws JsonMappingException, JsonProcessingException {
		// get a user json object from user-service
		log.info("Requesting user information through user.rpc call for: " + email);
	    String user = (String) rabbitTemplate.convertSendAndReceive(directExchange.getName(), "rpc", email);
	    if(user == null) {
	    	log.info("It was not possible to retrieve the user information, user-service is likely unavailable.");
	    	throw new UsernameNotFoundException("Username: " + email + " not found");
	    }
	    
	    // deserialize into UserCredentials and return
	    log.info("Received json user properties: " + user);
	    return deserializeToUserCredentials(user);
	}
	
	/**
	 * Fallback method in case the RPC communication channel fails. 
	 * It is important to note that if a user is not found because the username is incorrect, this fallback is not fired.
	 * 
	 * @param email The original requested email.
	 * @param hystrixCommand The hytrix exception that was captured.
	 * @return A UserDetails object based on  a guest user instance.
	 */
	public UserDetails loadGuestUser(String username, Throwable hystrixCommand) {
		UserCredentials user = getGuestUser(guestUserName, guestUserPass, guestUserRole);
		
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
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	private UserCredentials deserializeToUserCredentials(String user) throws JsonMappingException, JsonProcessingException {
		log.info("Deserializing user json into UserCredentials object");
		TypeReference<UserCredentials> mapType = new TypeReference<UserCredentials>() {};
	    ObjectMapper objectMapper = new ObjectMapper();
	    return objectMapper.readValue(user, mapType);
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