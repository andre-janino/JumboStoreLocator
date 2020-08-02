package com.jumbo.userservice.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumbo.userservice.entity.User;
import com.jumbo.userservice.exception.BadRequest;
import com.jumbo.userservice.exception.ResourceNotFound;
import com.jumbo.userservice.repository.UserRepository;
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

	@Autowired
	private UserRepository repository;
	
	@Override
	public List<User> findAll() {
		return repository.findAll();
	}

	@Override
	public User findOne(Long userId) {
		return repository.findById(userId).orElseThrow(() -> 
		new ResourceNotFound("User not found."));
	}

	@Override
	public User create(User user) {
		if(emailExists(user)) throw new BadRequest("Email '" + user.getEmail() + "' is already in use.");
		
		// encrypt the password and create the user
		user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
		return repository.save(user);
	}

	@Override
	public User update(Long userId, User user) {
		repository.findById(userId).orElseThrow(() -> 
		new ResourceNotFound("User not found."));
		return repository.save(user);
	}

	@Override
	public boolean delete(Long userId) {
		User found = repository.findById(userId).orElseThrow(() -> 
		new ResourceNotFound("User not found."));
		repository.delete(found);
		return true;
	}
	
	@Override
	public String getUserMessageRpc(String email) {
		// find a user by email, which in this implementation is considered as the login username
		User user = repository.findByEmail(email);
		
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
		return repository.findByEmail(user.getEmail()) != null;
	}
}