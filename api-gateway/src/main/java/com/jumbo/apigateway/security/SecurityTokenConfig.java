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
			   .antMatchers(HttpMethod.POST, "/auth/**").permitAll() // everyone is allowed to authenticate
			   
			   .antMatchers(HttpMethod.GET, "/user/users/**").hasAnyRole("ADMIN","USER") // allow admin and users to get their info (guests not allowed)
			   .antMatchers(HttpMethod.POST, "/user/users/**").hasRole("ADMIN") // manual creation of users is only allowed by admins
			   .antMatchers(HttpMethod.PUT, "/user/users/**").hasRole("ADMIN") // manual update of users is only allowed by admins
			   .antMatchers(HttpMethod.DELETE, "/user/users/**").hasRole("ADMIN") // manual deletion of users is only allowed by admins
			   
			   .antMatchers(HttpMethod.GET, "/store/**").permitAll() // everyone is able to list stores/favorites
			   .antMatchers(HttpMethod.POST, "/store/favorites/**").permitAll() // allowing everyone to call favorite/unfavoriting on this dev env. TODO: remove the permitAlls and uncomment the two lines below.
			   .antMatchers(HttpMethod.DELETE, "/store/favorites/**").permitAll() 
			   //.antMatchers(HttpMethod.POST, "/store/favorites/**").hasAnyRole("ADMIN","USER") // guests are not allowed to favorite 
			   //.antMatchers(HttpMethod.DELETE, "/store/favorites/**").hasAnyRole("ADMIN","USER") // guests are not allowed to unfavorite
			   
			   .antMatchers(HttpMethod.POST, "/store/stores/**").hasRole("ADMIN") // manual creation of stores is only allowed by admins (and not currently supported by the UI)
			   .antMatchers(HttpMethod.PUT, "/store/stores/**").hasRole("ADMIN") // manual update of stores is only allowed by admins (and not currently supported by the UI)
			   .antMatchers(HttpMethod.DELETE, "/store/stores/**").hasRole("ADMIN") // manual deletion of a store is only allowed by admins (and not currently supported by the UI)
			   .anyRequest().authenticated(); 
	}
}
