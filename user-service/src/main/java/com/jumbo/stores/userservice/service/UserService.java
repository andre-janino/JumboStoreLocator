package com.jumbo.stores.userservice.service;

import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import com.jumbo.stores.userservice.entity.User;

/**
 * CRUDE service interface for the User entity.
 *  
 * @author André Janino
 */
public interface UserService {
	
	public List<User> findAll();
	public User findOne(@PathVariable("id") Long userId);
	public User create(@RequestBody User user);
	public User update(@PathVariable("id") Long id, @RequestBody User user);
	public void delete(@PathVariable("id") Long id);
}
