package com.jumbo.userservice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * User entity class with auto-generated ID, first and last name and email.
 * Could also be expanded to require phone number and other info.
 * 
 * Spring Boot auto-configuration detects that in-memory H2 database is being
 * used and auto-creates the DB based on this entity at "jdbc:h2:mem:testdb".
 * For a real project, it would be better to use MySQL/PostgreSQL for this
 * purpose.
 * 
 * @author André Armstrong
 */
@Entity(name = "Users")
public class User {

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private String firstName;

	private String lastName;

	@Email
	@NotEmpty
	@Column(unique = true)
	private String email;

	@Size(min = 6)
	private String password;

	@ManyToOne
    @JoinColumn (name = "role_id")
	@JsonProperty("role")
    private Role role;

	public User() {}

	public User(String firstName) {
		this.firstName = firstName;
	}

	public User(String firstName, String email) {
		this.firstName = firstName;
		this.email = email;
	}

	public User(Long id, String firstName, String email, String password) {
		this.id = id;
		this.firstName = firstName;
		this.email = email;
		this.password = password;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
}
