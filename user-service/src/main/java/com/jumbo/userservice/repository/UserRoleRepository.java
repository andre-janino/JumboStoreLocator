package com.jumbo.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jumbo.userservice.entity.Role;

/**
 * Repository that manages user roles. 
 * 
 * @author André Janino
 */
@Repository
public interface UserRoleRepository extends JpaRepository<Role, Long> {
    
	public Role findByName(String firstName);
}