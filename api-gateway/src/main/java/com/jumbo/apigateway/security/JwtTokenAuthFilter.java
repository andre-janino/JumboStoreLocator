package com.jumbo.apigateway.security;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
public class JwtTokenAuthFilter extends  OncePerRequestFilter {
	
	// prefix/key properties. TODO: move it all to a centralized and safe cloud config.
	private static final String PREFIX = "Bearer";
	private static final String SECRET_KEY = "JwtSecretKey";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
		// get the authentication header and check if it is valid
		String header = request.getHeader("Authorization");
		if(header == null || !header.startsWith(PREFIX)) {
			authenticate(header);
		}
		// move forward with the filter chain
		chain.doFilter(request, response);
	}

	/**
	 * Authenticate the user based on the provided token
	 * 
	 * @param header
	 */
	private void authenticate(String header) {
		try {
			// extract the needed information from the token
			String token = header.replace(PREFIX, "");		
			Claims claims = Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(token).getBody();
			String username = claims.getSubject();
			if(username != null) {
				@SuppressWarnings("unchecked")
				List<String> authorities = (List<String>) claims.get("authorities");
				
				// create the auth object
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
				 
				// authenticate the user
				SecurityContextHolder.getContext().setAuthentication(auth);
			}	
		} catch (Exception e) {
			SecurityContextHolder.clearContext();
		}
	}
}