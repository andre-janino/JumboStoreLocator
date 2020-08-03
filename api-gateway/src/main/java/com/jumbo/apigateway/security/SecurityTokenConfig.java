package com.jumbo.apigateway.security;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * This class implements the API gateway security configurations.
 * - Ensures a stateless session
 * - Validates JWT tokens through JwtTokenAuthFilter
 * - Allows all requests that goes towards /auth/**, and requires that every other request is authenticated. 
 * 
 * Future implementations could also consider the user role.
 * 
 * @author André Janino
 */
@EnableWebSecurity
public class SecurityTokenConfig extends WebSecurityConfigurerAdapter {
	
	@Value("${header:Authorization}")
	private String header; 

	@Value("${prefix:Bearer}")
	private String prefix;
	
	@Value("${secretKey:JwtSecretKey}")
	private String secretKey;
	
	@Override
  	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable() // disable csrf as we're using JWT
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) 	
		.and()
			.exceptionHandling().authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED)) 	
		.and()
			.addFilterAfter(new JwtTokenAuthFilter(header, prefix, secretKey), UsernamePasswordAuthenticationFilter.class)
		.authorizeRequests()
		   .antMatchers(HttpMethod.POST, "/auth/**").permitAll()  
		   .anyRequest().authenticated(); 
	}
}
