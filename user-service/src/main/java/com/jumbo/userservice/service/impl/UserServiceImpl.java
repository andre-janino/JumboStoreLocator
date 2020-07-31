package com.jumbo.userservice.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
	
	private boolean emailExists(User user) {
		return repository.findByEmail(user.getEmail()) != null;
	}
}