package com.jumbo.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jumbo.userservice.entity.User;

/**
 * Repository that manages users. 
 * 
 * @author André Janino
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	public User findByFirstName(String firstName);
	public User findByEmail(String email);
}
