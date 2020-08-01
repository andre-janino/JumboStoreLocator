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

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * This class is responsible for validating the user credentials and generating the JWT token.
 * 
 * @author André Janino
 */
public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	// It is a good practice that JWT tokens are expired after 15 minutes. However, for testing purposes, it is configured to last an hour.
	private static final int JWT_EXPIRATION = 60 * 60;
	
	// header/prefix/key properties. TODO: move it all to a centralized and safe cloud config.
	private static final String HEADER = "Authorization";
	private static final String PREFIX = "Bearer";
	private static final String SECRET_KEY = "JwtSecretKey";
	
	// object responsible for validating the user credentials
	private AuthenticationManager authManager;
    
	public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authManager) {
		this.authManager = authManager;
		
		// listen to "/auth" path
		this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/auth/**", "POST"));
	}
	
	/**
	 * Authenticate the user.
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		try {
			// Get credentials from request. TODO: move this to the user-service?
			UserCredentials creds = new ObjectMapper().readValue(request.getInputStream(), UserCredentials.class);
			
			// Create an object with user credentials for the auth manager
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(creds.getUserEmail(), creds.getPassword(), Collections.emptyList());
			
			// authenticate and load the user info
			return authManager.authenticate(authToken);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Generates a token with the user info. 
	 * 
	 * Token expires in JWT_EXPIRATION seconds.
	 */
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication auth) throws IOException, ServletException {	
		Long now = System.currentTimeMillis();
		String token = Jwts.builder()
			.setSubject(auth.getName())	
			.claim("authorities", auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
			.setIssuedAt(new Date(now))
			.setExpiration(new Date(now + JWT_EXPIRATION * 1000)) 
			.signWith(SignatureAlgorithm.HS512, SECRET_KEY.getBytes())
			.compact();
		
		// Add the token to header
		response.addHeader(HEADER, PREFIX + token);
	}
	
	/**
	 * Temp class to represent the user credentials.
	 * 
	 * @author André Janino
	 */
	private static class UserCredentials {
	    private String email, password;    
	    
	    public String getUserEmail() {
			return email;
		}
	    public String getPassword() {
			return password;
		}
	}
}