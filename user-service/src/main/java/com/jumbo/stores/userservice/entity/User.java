package com.jumbo.stores.userservice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

/**
 * User entity class with auto-generated ID, first and last name and email. Could also be expanded to require phone number and other info.
 * 
 * Spring Boot auto-configuration detects that in-memory H2 database is being used and auto-creates the DB based on this entity at "jdbc:h2:mem:testdb".
 * For a real project, it would be better to use MySQL/PostgreSQL for this purpose. 
 * 
 * TODO: add roles for admin/standard user.
 * 
 * @author André Armstrong
 */
@Entity(name = "Users")
public class User {
	
	@Id
	@GeneratedValue
	private Long id;	
	private String firstName;
	private String lastName;
	@Email
    @NotEmpty
	@Column(unique=true)
	private String email;
	@JsonProperty(access = Access.WRITE_ONLY)
	@Size(min=6)
    private String password;
	
    public User() {}
	
	public Long getId() {
		return id;
	}
	
	public String getFirstName() {
		return firstName;
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
}
