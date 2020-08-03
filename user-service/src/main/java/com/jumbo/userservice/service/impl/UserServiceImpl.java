package com.jumbo.userservice.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumbo.userservice.entity.Role;
import com.jumbo.userservice.entity.User;
import com.jumbo.userservice.exception.BadRequest;
import com.jumbo.userservice.exception.ResourceNotFound;
import com.jumbo.userservice.repository.UserRepository;
import com.jumbo.userservice.repository.UserRoleRepository;
import com.jumbo.userservice.service.UserService;

/**
 * CRUDE service implementation for the User entity.
 * 
 * Returns errors (BadRequest and ResourceNotFound) in case the rest command was malformed or no user was found, respectively.
 * 
 * Aside from REST HTTP calls, this service also handles RabbitMQ RPC calls.
 * 
 * @author André Janino
 */
@Service
public class UserServiceImpl implements UserService {
	
	@Value("${userNotFound:NOT_FOUND}")
	private String notFound;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserRoleRepository roleRepository;
	
	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}

	@Override
	public User findOne(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> 
		new ResourceNotFound("User not found."));
	}

	@Override
	public User create(User user) {
		if(emailExists(user)) throw new BadRequest("Email '" + user.getEmail() + "' is already in use.");
		
		// if no role is provided, assign a user role and save
		assignDefaultRole(user);
		return userRepository.save(user);
	}

	private void assignDefaultRole(User user) {
		if(user.getRole() == null) {
			Role role = roleRepository.findByName("USER");
			if(role != null) {
				user.setRole(role);
			}
		}
	}

	@Override
	public User update(Long userId, User user) {
		userRepository.findById(userId).orElseThrow(() -> 
		new ResourceNotFound("User not found."));
		return userRepository.save(user);
	}

	@Override
	public boolean delete(Long userId) {
		User found = userRepository.findById(userId).orElseThrow(() -> 
		new ResourceNotFound("User not found."));
		userRepository.delete(found);
		return true;
	}
	
	@Override
	public String getUserMessageRpc(String email) {
		// find a user by email, which in this implementation is considered as the login username
		User user = userRepository.findByEmail(email);
		
		// if no user is present, return a message stating so (so that the consumer knows whether it failed due to a timeout or a user that does not exist)
		if(user == null) {
			return notFound;
		}
		
		// convert the found user into json, and return
		return serializeToJson(user);
	}
	
	/**
	 * Serialize a user into json for RabbitMQ messages.
	 * 
	 * If this class gets called at another place, move it to an utility class.
	 * 
	 * @param user
	 * @return a json serialized user object
	 */
	private String serializeToJson(User user) {
	    ObjectMapper mapper = new ObjectMapper();
	    String jsonInString = "";
	    try {
	        jsonInString = mapper.writeValueAsString(user);
	    } catch (JsonProcessingException e) {
	        System.out.println(String.valueOf(e));
	    }
	    return jsonInString;
	}
	
	private boolean emailExists(User user) {
		return userRepository.findByEmail(user.getEmail()) != null;
	}
}