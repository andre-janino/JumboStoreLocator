package com.jumbo.authservice.security;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * This class is responsible for validating the user credentials and generating the JWT token.
 * 
 * @author André Janino
 */
public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	private static final String GUEST_USER = "Guest";
	private static final String GUEST_ROLE = "ROLE_GUEST";
	private static final String AUTH_GUEST = "/auth/guest";
	
	private String header; 
	private String prefix;
	private String secretKey;
	private Integer jwtExpiration; 
	
	private AuthenticationManager authManager;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
    
	/**
	 * Guest user singleton, employed for guest login alternative.
	 */
	private static UserCredentials guestUser;
	private UserCredentials getGuestUser() {
		if (guestUser == null){ 
			setGuestUser(GUEST_USER, GUEST_ROLE);
		}
		return guestUser;
	}
    private static void setGuestUser(String guestUserName, String guestUserRole) {
        guestUser = new UserCredentials(guestUserName, guestUserRole);
    }
	
	/**
	 * Initialize the JWT token properties.
	 * 
	 * Note: Perhaps it would be better to encapsulate all the Jwt properties on a class. 
	 * Given that both auth-service and api-gateway make use of it, perhaps it would be better if this was defined on an independent project imported by both.
	 * 
	 * @param authManager Responsible for validating the user credentials
	 * @param header The name of the header parameter that holds the token.
	 * @param prefix The string that represents the prefix of the JWT token.
	 * @param secretKey The secret key used to parse the JWT token.
	 * @param jwtExpiration The expiration time of the JWT token (1h by default).
	 * @param guestUserPass 
	 */
	public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authManager, String header, String prefix, String secretKey, Integer jwtExpiration) {
		log.info("JwtUsernameAndPasswordAuthenticationFilter started.");
		this.authManager = authManager;
		this.header = header;
		this.prefix = prefix;
		this.secretKey = secretKey;
		this.jwtExpiration = jwtExpiration;
		
		// listen to "/auth" path
		this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/auth/**", "POST"));
	}
	
	/**
	 * Extract user credentials and authenticate the user. If it is a guest login, pass GUEST_USER forward.
	 * 
	 * @param request The HTTP request object, used to extract the user credentials.
	 * @param response The HTTP servlet response object, unused on this particular implementation.
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		try {	
			// short-circuit the process for guest users, providing read-only access
			if(AUTH_GUEST.equals(request.getRequestURI())) {
				return new UsernamePasswordAuthenticationToken(getGuestUser(), "", Collections.emptyList());
			}
			return authenticate(request);
		} catch (IOException e) {
			log.error("There was a problem authenticating the user.", e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Authenticate the user with the provided credentials.
	 * 
	 * @param request The HTTP request object, used to extract the user credentials.
	 * @return
	 * @throws IOException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 */
	public Authentication authenticate(HttpServletRequest request) throws IOException, JsonParseException, JsonMappingException {
		// Get credentials from request.
		UserCredentials creds = new ObjectMapper().readValue(request.getInputStream(), UserCredentials.class);
		log.info("Attempting to authenticate user " + creds.getEmail());
		
		// Create an object with user credentials for the auth manager
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), Collections.emptyList());
		
		// authenticate and load the user info
		log.info("Authenticating the user.");
		return authManager.authenticate(authToken);
	}

	/**
	 * Generates a token with the user info and append it on the response header. 
	 * 
	 * Token expires in jwtExpiration seconds (1 hour by default).
	 * 
	 * @param request The HTTP request object, not used in this particular implementation
	 * @param response The response object, which is filled with the authentication header info
	 * @param chain A chain of invocations
	 * @param auth The authentication object containing user credentials
	 */
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication auth) throws IOException, ServletException {	
		Long now = System.currentTimeMillis();
		String token = Jwts.builder()
			.setSubject(auth.getName())	
			.claim("authorities", auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
			.setIssuedAt(new Date(now))
			.setExpiration(new Date(now + jwtExpiration * 1000)) 
			.signWith(SignatureAlgorithm.HS512, secretKey.getBytes())
			.compact();
		
		// Add the token to header
		log.info("Auth token generated successfully: " + token);
		response.addHeader(header, prefix + token);
		
		// Add the user info to the body
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
	
		// Create an object mapper with disabled auto detection (only include annotated fields)
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.AUTO_DETECT_CREATORS, MapperFeature.AUTO_DETECT_FIELDS, MapperFeature.AUTO_DETECT_GETTERS, MapperFeature.AUTO_DETECT_IS_GETTERS);
		try {
		  String json = mapper.writeValueAsString(auth.getPrincipal());
		  System.out.println("ResultingJSONstring = " + json);
		  response.getWriter().write(json);
		} catch (JsonProcessingException e) {
		   log.error("There was an error while building the user response object.", e);
		}
	}
	
	/**
	 * Temp class to represent the user credentials.
	 * 
	 * @author André Janino
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	static class UserCredentials {
		@JsonProperty("firstName")
		private String firstName;
		
		@JsonProperty("lastName")
		private String lastName;
		
		@JsonProperty("email")
		private String email;
		
		@JsonProperty("role")
		private String role;   
		
	    private String password;
	    
	    UserCredentials() {}
	    UserCredentials(String username, String role) {
	    	this.firstName = username; // when creating a guest user, just set the name as the "email"
	    	this.email = username;
	    	this.role = role;
	    }
	    
	    public String getFirstName() {
	    	return firstName;
	    }
	    public String getLastName() {
	    	return lastName;
	    }
	    public String getPassword() {
			return password;
		}
	    public String getEmail() {
			return email;
		}
	    public String getRole() {
	    	return role;
	    }
	}
}