package com.jumbo.userservice.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.jumbo.userservice.entity.User;
import com.jumbo.userservice.exception.BadRequest;
import com.jumbo.userservice.exception.ResourceNotFound;
import com.jumbo.userservice.repository.UserRepository;
import com.jumbo.userservice.service.UserService;
import com.jumbo.userservice.service.impl.UserServiceImpl;

/**
 * JUnit tests for the user service. 
 * 
 * @author André Janino
 */
@RunWith(SpringRunner.class)
public class UserServiceImplIntegrationTest {

	private static final String USER_PASS_1 = "password1";
	private static final String USER_PASS_2 = "password2";
	private static final String USER_PASS_3 = "password3";
	
	private static final String USER_EMAIL_1 = "email_user_1@gmail.com";
	private static final String USER_EMAIL_2 = "email_user_2@gmail.com";
	private static final String USER_EMAIL_3 = "email_user_3@gmail.com";
	
	private static final long USER_ID_1 = 1l;
	private static final long USER_ID_2 = 2l;
	private static final long USER_ID_3 = 3l;
	
	private static final String USER_NAME_1 = "John";
	private static final String USER_NAME_2 = "Mary";
	private static final String USER_NAME_3 = "Invalid";
	
	User user1;
	User user2;
	User user3;

	@TestConfiguration
    static class UserServiceImplTestContextConfiguration {
 
        @Bean
        public UserService userService() {
            return new UserServiceImpl();
        }
    }
	
	@Autowired
    private UserService service;
 
    @MockBean
    private UserRepository repository;
	
    /**
     * Setup the repository returns for two users, and initialize a third one, that is not present on the db.
     */
    @Before
    public void setUp() {
        user1 = new User(USER_ID_1, USER_NAME_1, USER_EMAIL_1, USER_PASS_1);
        user2 = new User(USER_ID_2, USER_NAME_2, USER_EMAIL_2, USER_PASS_2);
        user3 = new User(USER_ID_3, USER_NAME_3, USER_EMAIL_3, USER_PASS_3);
        
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        
        given(repository.findById(1l)).willReturn(Optional.of(user1));
        given(repository.findById(2l)).willReturn(Optional.of(user2));
        given(repository.findAll()).willReturn(users);
       
        given(repository.save(user1)).willReturn(user1);
        given(repository.save(user2)).willReturn(user2);
        given(repository.save(user3)).willReturn(user3);
      
        given(repository.findByEmail(USER_EMAIL_1)).willReturn(user1);
        given(repository.findByEmail(USER_EMAIL_2)).willReturn(user2);
    }
    
    @Test
    public void userServiceInjected_NotNull() throws Exception {
        assertThat(service).isNotNull();
    }
    
    @Test
	public void findAll_ReturnUsers() {
		 List<User> found = service.findAll();
		 assertThat(found.size()).isEqualTo(2);
	}
    
	@Test
	public void findOne_ValidId_ReturnUser() {
		 User found = service.findOne(USER_ID_1);
		 assertThat(found.getFirstName()).isEqualTo(USER_NAME_1);
	}
	
	@Test(expected = ResourceNotFound.class)
	public void findOne_InvalidId_ReturnNotFound() {
		 service.findOne(USER_ID_3);	
	}
	
	@Test
	public void create_NewUser_ReturnUser() {
		 User created = service.create(user3);
		 assertThat(created).isEqualTo(user3);
	}
	
	@Test(expected = BadRequest.class)
	public void create_ExistingUser_ReturnBadRequest() {
		service.create(user1);
	}
	
	@Test
	public void update_ValidId_ReturnUser() {
		 User updated = service.update(USER_ID_1, user1);
		 assertThat(updated).isEqualTo(user1);
	}
	
	@Test(expected = ResourceNotFound.class)
	public void update_InvalidId_ReturnNotFound() {
		service.update(USER_ID_3, user3);
	}
	
	@Test
	public void delete_ValidId_ReturnTrue() {
		 boolean result = service.delete(USER_ID_1);
		 assertThat(result).isEqualTo(true);
	}
	
	@Test(expected = ResourceNotFound.class)
	public void delete_InvalidId_ReturnNotFound() {
		 service.delete(USER_ID_3);
	}
}
