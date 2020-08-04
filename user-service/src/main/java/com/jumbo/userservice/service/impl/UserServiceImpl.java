package com.jumbo.userservice.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Return all existing users.
	 */
	@Override
	public List<User> findAll() {
		log.info("[Find all users] called.");
		List<User> users = userRepository.findAll();
		log.info("Found" + users.size() + " user(s), returning.");
		return users;
	}

	/**
	 * Find a user by id.
	 * 
	 * @param userId The id of the user that should be retrieved
	 * @return the found user object
	 */
	@Override
	public User findOne(Long userId) {
		log.info("[Find user] called for id '" + userId + "'");
		User user = userRepository.findById(userId).orElseThrow(() -> 
		new ResourceNotFound("User not found."));
		log.info("User found, returning.");
		return user;
	}

	/**
	 * Create a user.
	 * 
	 * @param user A user object that was serialized from a json object.
	 * @return the created user object
	 */
	@Override
	public User create(User user) {
		log.info("[Create user] called.");
		if(emailExists(user)) throw new BadRequest("Email '" + user.getEmail() + "' is already in use.");
		
		// if no role is provided, assign a user role and save
		assignDefaultRole(user);
		
		// save the user
		User createdUser = userRepository.save(user);
		log.info("User created successfully.");
		return createdUser;
	}

	/**
	 * Update a user.
	 * 
	 * @param userId The ID of the user that should be updated
	 * @param user A user object that was serialized from a json object.
	 * @return the found user object
	 */
	@Override
	public User update(Long userId, User user) {
		log.info("[Update user] called for id '" + user.getId() + "'");
		
		// verify if the user exists before updating
		userRepository.findById(userId).orElseThrow(() -> 
		new ResourceNotFound("User not found."));
		
		// save and return
		User savedUser = userRepository.save(user);
		log.info("User updated successfully.");
		return savedUser;
	}

	/**
	 * Delete a user.
	 * 
	 * @param userId The ID of the user that should be deleted
	 * @return true if the process goes to the end
	 */
	@Override
	public boolean delete(Long userId) {
		log.info("[Delete user] called for id '" + userId + "'");
		
		// check if the user exists
		User found = userRepository.findById(userId).orElseThrow(() -> 
		new ResourceNotFound("User not found."));
		
		// if so, delete and return true
		userRepository.delete(found);
		log.info("User deleted successfully.");
		return true;
	}
	
	/**
	 * Return a user json for RabbitMQ RPC requests
	 * 
	 * @param email The email of the user that should be retrieved
	 * @return A json object containing the user properties
	 */
	@Override
	public String getUserMessageRpc(String email) {
		log.info("[RPC Get user] called for email '" + email + "'");
		
		// find a user by email, which in this implementation is considered as the login username
		User user = userRepository.findByEmail(email);
		
		// if no user is present, return a message stating so (so that the consumer knows whether it failed due to a timeout or a user that does not exist)
		if(user == null) {
			log.info("User not found, returning error message.");
			return notFound;
		}
		
		// convert the found user into json, and return
		log.info("User found, serializing and returning.");
		return serializeToJson(user);
	}
	
	/**
	 * Serialize a user into json for RabbitMQ messages.
	 * If this class gets called at another place, move it to an utility class.
	 * 
	 * @param user
	 * @return a json serialized user object
	 */
	private String serializeToJson(User user) {
	    ObjectMapper mapper = new ObjectMapper();
	    String jsonInString = "";
	    try {
	    	log.info("Serializing user '" + user.getId() + "'");
	        jsonInString = mapper.writeValueAsString(user);
	    } catch (Exception e) {
	        System.out.println(String.valueOf(e));
	    }
	    log.info("Serialized user to json: " + jsonInString);
	    return jsonInString;
	}
	
	/**
	 * Verify if the given user has an email registered.
	 * 
	 * @param user The evaluated user
	 * @return true if an email is present, false otherwise
	 */
	private boolean emailExists(User user) {
		return userRepository.findByEmail(user.getEmail()) != null;
	}
	
	/**
	 * Assign a default role to a user, in case none was provided.
	 * 
	 * @param user The user object that will be created.
	 */
	private void assignDefaultRole(User user) {
		if(user.getRole() == null) {
			Role role = roleRepository.findByName("USER");
			if(role != null) {
				log.info("Default role set: " + role.getRoleName());
				user.setRole(role);
			} else {
				log.info("No default role found");
			}
		}
	}
}