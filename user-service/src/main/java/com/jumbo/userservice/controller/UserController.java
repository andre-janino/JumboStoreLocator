package com.jumbo.userservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jumbo.userservice.entity.User;
import com.jumbo.userservice.service.UserService;

/**
 * Request controller for the user entity.
 * 
 * Bad requests (BadRequest) and resources not found (ResourceNotFound) errors are handled on UserService.
 * 
 * @author André Janino
 */
@RestController
public class UserController {

	private UserService service;
	
	public UserController(UserService service) {
		this.service = service;
	}
	
	/**
	 * This method reaches out to the user service and return a json object containing a list of all users.
	 * In the current implementation, there's no use for this method, but it may be useful when we support admins.
	 * 
	 * @return a list of users
	 */
	@GetMapping("/users")
	public List<User> retrieveAllUsers(){
		return service.findAll();
	}
	
	/**
	 * This method reaches out to the user service and return a json object containing a single user. 
	 * 
	 * @param id the desired user
	 * @return a user object, or a resource not found message
	 */
	@GetMapping("/users/{id}")
	public User retrieveUser(@PathVariable long id) {
		return service.findOne(id);
	}
	
	/**
	 * This method reaches out to the user service and creates a new user.
	 * 
	 * @param user object to be created
	 * @return the created user object, or an error response
	 */
	@PostMapping("/users")
	public User createUser(@RequestBody User user) {
		return service.create(user);
	}
	
	/**
	 * This method reaches out to the user service and updates an existing user.
	 * 
	 * @param user object to be updated
	 * @param id of the user object
	 * @return the updated user object, or an error response
	 */
	@PutMapping("/users/{id}")
	public User updateUser(@RequestBody User user, @PathVariable long id) {
		return service.update(id, user);
	}
	
	/**
	 * This method reaches out to the user service and deletes an existing user.
	 * 
	 * @param id of the user to be deleted.
	 */
	@DeleteMapping("/users/{id}")
	public void deleteUser(@PathVariable long id) {
		service.delete(id);
	}
}
