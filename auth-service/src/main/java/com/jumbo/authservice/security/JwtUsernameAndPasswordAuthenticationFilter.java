package com.jumbo.authservice.security;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * This class is responsible for validating the user credentials and generating the JWT token.
 * 
 * @author André Janino
 */
public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	private String header; 
	private String prefix;
	private String secretKey;
	private Integer jwtExpiration; 
	
	private AuthenticationManager authManager;
    
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
	 */
	public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authManager, String header, String prefix, String secretKey, Integer jwtExpiration) {
		this.authManager = authManager;
		this.header = header;
		this.prefix = prefix;
		this.secretKey = secretKey;
		this.jwtExpiration = jwtExpiration;
		
		// listen to "/auth" path
		this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/auth/**", "POST"));
	}
	
	/**
	 * Extract user credentials and authenticate the user.
	 * 
	 * @param request The HTTP request object, used to extract the user credentials.
	 * @param response The HTTP servlet response object, unused on this particular implementation.
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		try {
			// Get credentials from request.
			UserCredentials creds = new ObjectMapper().readValue(request.getInputStream(), UserCredentials.class);
			
			// Create an object with user credentials for the auth manager
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(creds.getUsername(), creds.getPassword(), Collections.emptyList());
			
			// authenticate and load the user info
			return authManager.authenticate(authToken);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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
		response.addHeader(header, prefix + token);
	}
	
	/**
	 * Temp class to represent the user credentials.
	 * 
	 * @author André Janino
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	static class UserCredentials {
	    private String username, password, email, role;    
	    
	    UserCredentials() {}
	    UserCredentials(String username, String email, String password, String role) {
	    	this.username = username;
	    	this.email = email;
	    	this.password = password;
	    	this.role = role;
	    }
	    
	    public String getUsername() {
			return username;
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