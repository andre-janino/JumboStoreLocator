package com.jumbo.authservice.security;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * If the provided user is present on the database, create a Spring user object with the proper authorities
 * 
 * @author André Janino
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService  {
	
	// TODO: remove this hardcoded admin role handle it on the user-service.
	private static final String ROLE_ADMIN = "ROLE_ADMIN";

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		// hard-coding this check for now. TODO: implement communication between auth-service and user-service
		if("andre.janino.gmail.com".equals(username)) {
			// as roles are not yet implemented, issue an ADMIN role to our user
			List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(ROLE_ADMIN);
			
			// returns a Spring user, which is employed by UserDetailsService to manage the authentication
			return new User("andre.janino@gmail.com", "$2a$10$heirHA89ULwxENiWxaj25O1S9oRafpyvLQw21shSNWhV7i/VuZNJ6", grantedAuthorities);
		}
		
		// if this point is reached, user was not found; throw an exception
		throw new UsernameNotFoundException("Username: " + username + " not found");
	}
}