package com.jumbo.apigateway.security;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

/**
 * Responsible for validating JWT tokens.
 * 
 * @author André Janino
 */
public class JwtTokenAuthFilter extends OncePerRequestFilter {
	
	private String header; 
	private String prefix;
	private String secretKey;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * Initialize the JWT token properties
	 * 
	 * Note: Perhaps it would be better to encapsulate all the Jwt properties on a class. 
	 * Given that both auth-service and api-gateway make use of it, perhaps it would be better if this was defined on an independent project imported by both.
	 * 
	 * @param header The name of the header parameter that holds the token.
	 * @param prefix The string that represents the prefix of the JWT token.
	 * @param secret_key The secret key used to parse the JWT token. 
	 */
	public JwtTokenAuthFilter(String header, String prefix, String secretKey) {
		this.header = header;
		this.prefix = prefix;
		this.secretKey = secretKey;
	}

	/**
	 * If the authentication header is present and starts with the configured prefix, call the authenticate method.
	 * 
	 * @param request The HTTP request object, used to extract the user credentials.
	 * @param response The HTTP servlet response object, unused on this particular implementation.
	 * @param chain A chain of invocations
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
		log.info("Authentication method initiated.");
		
		// get the authentication header and check if it is valid
		String reqHeader = request.getHeader(header);
		if(reqHeader != null && reqHeader.startsWith(prefix)) {
			log.info(header + " header is present: " + reqHeader);
			authenticate(reqHeader);
		} else {
			log.info(header + " header not present.");
		}
		
		// move forward with the filter chain
		log.info("Processing done. Moving forward to other filters in the chain.");
		chain.doFilter(request, response);
	}

	/**
	 * Authenticate the user based on the provided JWT token.
	 * 
	 * @param header The authorization header object
	 */
	private void authenticate(String reqHeader) {
		try {
			// extract the needed information from the token
			String token = reqHeader.replace(prefix, "");		
			Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody();
			String username = claims.getSubject();
			if(username != null) {
				log.info("Attempting authentication for user: " + username);
				
				@SuppressWarnings("unchecked")
				List<String> authorities = (List<String>) claims.get("authorities");
				
				// create the auth object
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
				 
				// authenticate the user
				SecurityContextHolder.getContext().setAuthentication(auth);
			} else {
				log.info("User information not found on the " + header + " header, it was not possible to authenticate the user.");
			}
		} catch (Exception e) {
			log.error("There was a problem authenticating the user.", e);
			SecurityContextHolder.clearContext();
		}
	}
}