package com.jumbo.userservice.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Role entity class with auto-generated ID, role name and list of users. Many-to-Many relation with User entity.
 * 
 * Spring Boot auto-configuration detects that in-memory H2 database is being used and auto-creates the DB based on this entity at "jdbc:h2:mem:testdb".
 * For a real project, it would be better to use MySQL/PostgreSQL for this purpose. 
 * 
 * @author André Armstrong
 */
@Entity
public class Role {

	@Id
	@GeneratedValue
	@Column(nullable = false, columnDefinition = "smallint unsigned")
	@JsonIgnore
	private Integer id;

	@Column(unique = true)
	@JsonValue
	private String name;

	@JsonIgnore
	@OneToMany (mappedBy = "role")
    private Set<User> users;

	public Integer getRoleId() {
		return id;
	}

	public void setRoleId(Integer id) {
		this.id = id;
	}

	public String getRoleName() {
		return name;
	}

	public void setRoleName(String name) {
		this.name = name;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}
}