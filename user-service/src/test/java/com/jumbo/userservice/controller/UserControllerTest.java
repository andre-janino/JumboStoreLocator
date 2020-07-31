package com.jumbo.userservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.jumbo.userservice.controller.UserController;
import com.jumbo.userservice.entity.User;
import com.jumbo.userservice.exception.BadRequest;
import com.jumbo.userservice.exception.ResourceNotFound;
import com.jumbo.userservice.service.UserService;

/**
 * JUnit tests for the user service controller. 
 * 
 * Tests good and bad requests for retrieveAllUsers, retrieveUser, createUser, updateUser and deleteUser
 * 
 * @author André Janino
 */
@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

	private static final String USER_OBJECT = "{\"id\": 1001, \"firstName\": \"André\", \"lastName\": \"Armstrong Janino Cizotto\", \"email\": \"andre.janino@gmail.com\"}";

	@Autowired
    UserController controller;
	
	@MockBean 
	private UserService service;
	
	@Autowired
    private MockMvc mvc;
	
	@Test
    public void userControllerInjected_NotNull() throws Exception {
        assertThat(controller).isNotNull();
    }
	
	@Test
	public void retrieveAllUsers_NoUsers_ReturnsOk() throws Exception {
		// mock an empty user set response from the user service
		List<User> allUsers = new ArrayList<>();	
		given(service.findAll()).willReturn(allUsers);
		
		// verify that the controller response is empty
		mvc.perform(get("/users")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk())
			      .andExpect(jsonPath("$", hasSize(0)));	      
	}
	
	@Test
	public void retrieveAllUsers_ValidUsers_ReturnsOk() throws Exception {
		// declare two valid users
		User user1 = new User("John");	
		User user2 = new User("Mary");	
		
		// mock the service response
		List<User> allUsers = Arrays.asList(user1,user2);	
		given(service.findAll()).willReturn(allUsers);
		
		// verify that the controller response has the mocked users
		mvc.perform(get("/users")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk())
			      .andExpect(jsonPath("$", hasSize(2)))
			      .andExpect(jsonPath("$[0].firstName", is(user1.getFirstName())))
			      .andExpect(jsonPath("$[1].firstName", is(user2.getFirstName())));	      
	}
	
	@Test
	public void retrieveUser_ValidUser_ReturnsOk() throws Exception {
		// mock two users to be returned from the user service
		User user = new User("John");	

		// mock the service response
		given(service.findOne(any(Long.class))).willReturn(user);
		
		// verify that the controller response has the mocked user
		mvc.perform(get("/users/1")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk())
			      .andExpect(jsonPath("$.firstName", is(user.getFirstName())));	      
	}
	
	@Test
	public void retrieveUser_NoUser_ReturnsNotFound() throws Exception {
		// mock the service response to throw a resource not found error
		given(service.findOne(any(Long.class))).willThrow(new ResourceNotFound("User not found."));
		
		// verify that the controller response throws a 404 error
		mvc.perform(get("/users/1")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isNotFound());	      
	}
	
	@Test
	public void createUser_ValidUser_ReturnsOk() throws Exception {
		// mock two users to be returned from the user service
		User user = new User("John");	

		// mock the service response
		given(service.create(any(User.class))).willReturn(user);
		
		// verify that the controller response has the mocked user
		mvc.perform(post("/users/")
				  .content(USER_OBJECT)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk());	      
	}
	
	@Test
	public void createUser_ExistingUser_ReturnsBadRequest() throws Exception {
		// mock the service response to throw a bad request
		given(service.create(any(User.class))).willThrow(new BadRequest("User already exists"));
		
		// verify that the controller response throws a 400 error
		mvc.perform(post("/users/")
				  .content(USER_OBJECT)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isBadRequest());	      
	}
	
	@Test
	public void createUser_NoParameter_ReturnsBadRequest() throws Exception {
		// verify that the controller response throws a 400 error when no user object is provided
		mvc.perform(post("/users/")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isBadRequest());	      
	}
	
	@Test
	public void updateUser_ValidUser_ReturnsOk() throws Exception {	
		// mock two users to be returned from the user service
		User user = new User("John");	

		// mock the service response
		given(service.update(any(Long.class), any(User.class))).willReturn(user);
		
		// verify that the controller response has the mocked user
		mvc.perform(put("/users/1")
				  .content(USER_OBJECT)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk());	      
	}
	
	@Test
	public void updateUser_NoUser_ReturnsNotFound() throws Exception {	
		// mock the service response to throw a bad request
		given(service.update(any(Long.class), any(User.class))).willThrow(new ResourceNotFound("User not found"));
		
		// verify that the controller response throws a 404 error
		mvc.perform(put("/users/1")
				  .content(USER_OBJECT)
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isNotFound());	      
	}
	
	@Test
	public void updateUser_NoParameter_ReturnsBadRequest() throws Exception {	
		// verify that the controller response throws a 400 error when no user object is provided
		mvc.perform(put("/users/")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isMethodNotAllowed());	      
	}
	
	@Test
	public void deleteUser_ValidUser_ReturnsOk() throws Exception {	
		// mock the service response
		given(service.delete(any(Long.class))).willReturn(true);
		
		// verify that the controller response has the mocked user
		mvc.perform(get("/users/1")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk());	      
	}
	
	@Test
	public void deleteUser_NoUser_ReturnsNotFound() throws Exception {	
		// mock the service response to throw a resource not found error
		given(service.delete(any(Long.class))).willThrow(new ResourceNotFound("User not found."));
		
		// verify that the controller response throws a 404 error
		mvc.perform(delete("/users/1")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isNotFound());	      
	}
	
	@Test
	public void deleteUser_NoParameter_ReturnsBadRequest() throws Exception {
		// verify that the controller response throws a 400 error when no user id is provided
		mvc.perform(delete("/users/")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isMethodNotAllowed());	      
	}
}
