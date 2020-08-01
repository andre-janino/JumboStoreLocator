package com.jumbo.authservice.security;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Similarly to API Gateway SecurityTokenConfig, this class implements the security configurations, but it also concerns itself with the user info.
 * 
 * Ensures a stateless session
 * Validates user credentials through JwtUsernameAndPasswordAuthenticationFilter
 * Allows all requests that goes towards /auth/**, and requires that every other request is authenticated. 
 * 
 * Future implementations could also consider the user role.
 * 
 * @author André Janino
 */
@EnableWebSecurity 
public class SecurityCredentialsConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		    .csrf().disable() // disable csrf as we're using JWT
		    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	    .and()
	    	.exceptionHandling().authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED))
        .and()
        	.addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager()))	
        .authorizeRequests()
	    	.antMatchers(HttpMethod.POST, "/auth/**").permitAll()
	    	.anyRequest().authenticated();
	}
	
	/**
	 * Load the user object from the database and set the password encoder
	 * 
	 * @param auth builder
	 * @throws Exception
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}
	
	/**
	 * Define the password encoder.
	 * 
	 * @return the password encoder object
	 */
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
}