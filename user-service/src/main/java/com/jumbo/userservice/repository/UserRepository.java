package com.jumbo.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jumbo.userservice.entity.User;

/**
 * Repository interface that manages users. 
 * 
 * At this point in time, it may be that updating/creating and deleting won't be supported in the first version by the front-end,
 * but the user service should be ready for it.
 * 
 * @author André Janino
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	public User findByFirstName(String firstName);
	public User findByEmail(String email);
}
