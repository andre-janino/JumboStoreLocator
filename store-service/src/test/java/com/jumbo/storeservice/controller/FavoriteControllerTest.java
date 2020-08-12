package com.jumbo.storeservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import com.jumbo.storeservice.entity.Favorite;
import com.jumbo.storeservice.exception.BadRequest;
import com.jumbo.storeservice.service.FavoriteService;

/**
 * JUnit tests for the store-service controller. 
 * 
 * @author André Janino
 */
@RunWith(SpringRunner.class)
@WebMvcTest(FavoriteController.class)
@AutoConfigureMockMvc
public class FavoriteControllerTest {
	
	@Autowired
    FavoriteController controller;
	
	@MockBean 
	private FavoriteService service;
	
	@Autowired
    private MockMvc mvc;
	
	@Test
    public void favoriteControllerInjected_NotNull() throws Exception {
        assertThat(controller).isNotNull();
    }
	
	@Test
	public void findAllFavorites_NoUserName_ReturnsBadReques() throws Exception {
		// verify that the controller response is empty
		mvc.perform(get("/favorites/")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isBadRequest());	      
	}
	
	@Test
	public void findAllFavorites_ValidUser_NoFavorites_ReturnsEmpty() throws Exception {
		// mock an empty store set response
		List<Favorite> allFavorites = new ArrayList<>();	
		given(service.findAllFavorites(any())).willReturn(allFavorites);
		
		// verify that the controller response is empty
		mvc.perform(get("/favorites/?userName=John")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk())
			      .andExpect(jsonPath("$", hasSize(0)));	      
	}
	
	@Test
	public void findAllStores_ValidStores_ReturnsOk() throws Exception {
		// declare two valid favorites
		Favorite favorite1 = new Favorite();	
		favorite1.setStoreId("1");
		Favorite favorite2 = new Favorite();	
		favorite2.setStoreId("2");
		
		// mock the service response
		List<Favorite> allFavorites = Arrays.asList(favorite1, favorite2);		
		given(service.findAllFavorites(any())).willReturn(allFavorites);
		
		// verify that the controller response is empty
		mvc.perform(get("/favorites/?userName=John")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk())
			      .andExpect(jsonPath("$", hasSize(2)))
			      .andExpect(jsonPath("$[0].storeId", is("1")))
			      .andExpect(jsonPath("$[1].storeId", is("2")));	      
	}
	
	@Test
	public void createFavorite_NoParams_ReturnsBadRequest() throws Exception {	
		// verify that the controller response throws a 400 error when no user object is provided
		mvc.perform(post("/favorites/")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isBadRequest());	      
	}
	
	@Test
	public void createFavorite_ExistingFavorite_ReturnsBadRequest() throws Exception {
		// mock the service response to throw a bad request
		given(service.addFavorite(any(), any())).willThrow(new BadRequest("User already favorited this store."));
		
		// verify that the controller response throws a 400 error
		mvc.perform(post("/favorites/?&userName=John&storeId=1234")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isBadRequest());	      
	}
	
	@Test
	public void createFavorite_ValidParams_ReturnsOk() throws Exception {
		Favorite favorite = new Favorite();	
		favorite.setStoreId("1234");
		favorite.setUserName("John");
		
		// mock the service response
		given(service.addFavorite(any(), any())).willReturn(favorite);
				
		// verify that the controller response has the requested favorite
		mvc.perform(post("/favorites/?&userName=" + favorite.getUserName() + "&storeId=" + favorite.getStoreId())
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk());
	}
	
	@Test
	public void deleteFavorite_NoUserName_ReturnsBadRequest() throws Exception {	
		// verify that the controller response throws a 400 error when no user object is provided
		mvc.perform(delete("/favorites/")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isBadRequest());	      
	}
	
	@Test
	public void deleteFavoriter_ValidParams_ReturnsOk() throws Exception {	
		// mock the service response
		given(service.removeFavorite(any(), any())).willReturn(true);
		
		// verify that the controller response has the mocked user
		mvc.perform(get("/favorites/?&userName=John&storeId=123")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk());	      
	}
}
