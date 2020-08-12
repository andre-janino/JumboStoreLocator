package com.jumbo.storeservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

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

import com.jumbo.storeservice.entity.Store;
import com.jumbo.storeservice.service.StoreService;

/**
 * JUnit tests for the store-service controller. 
 * 
 * @author André Janino
 */
@RunWith(SpringRunner.class)
@WebMvcTest(StoreController.class)
@AutoConfigureMockMvc
public class StoreControllerTest {
	
	@Autowired
    StoreController controller;
	
	@MockBean 
	private StoreService service;
	
	@Autowired
    private MockMvc mvc;
	
	@Test
    public void storeControllerInjected_NotNull() throws Exception {
        assertThat(controller).isNotNull();
    }
	
	@Test
	public void findAllStores_NoStores_ReturnsEmpty() throws Exception {
		// mock an empty store set response
		List<Store> allStores = new ArrayList<>();	
		given(service.findAllStores(any(), anyInt())).willReturn(allStores);
		
		// verify that the controller response is empty
		mvc.perform(get("/stores/")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk())
			      .andExpect(jsonPath("$", hasSize(0)));	      
	}
	
	@Test
	public void findAllStores_ValidStores_ReturnsOk() throws Exception {
		// declare two valid stores
		Store store1 = new Store();	
		store1.setAddressName("CITY_1");
		Store store2 = new Store();	
		store2.setAddressName("CITY_2");
		
		// mock the service response
		List<Store> allStores = Arrays.asList(store1,store2);	
		given(service.findAllStores(anyList(), anyInt())).willReturn(allStores);
		
		// verify that the controller response has the mocked stores
		mvc.perform(get("/stores/")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk())
			      .andExpect(jsonPath("$", hasSize(2)))
			      .andExpect(jsonPath("$[0].addressName", is(store1.getAddressName())))
			      .andExpect(jsonPath("$[1].addressName", is(store2.getAddressName())));	      
	}
	
	@Test
	public void findNearestStores_NoLatLng_ReturnsBadRequest() throws Exception {
		// verify that the controller response has the mocked stores
		mvc.perform(get("/stores/nearest/")
			      .contentType(MediaType.APPLICATION_JSON))
				  .andExpect(status().isBadRequest());	      
	}
	
	@Test
	public void findNearestStores_ValidStores_ReturnsOk() throws Exception {
		// declare two valid stores
		Store store1 = new Store();	
		store1.setAddressName("CITY_1");
		Store store2 = new Store();	
		store2.setAddressName("CITY_2");
		
		// mock the service response
		List<Store> allStores = Arrays.asList(store1,store2);	
		given(service.findNearestStores(anyDouble(), anyDouble(), anyList(), anyInt())).willReturn(allStores);
		
		// verify that the controller response has the mocked stores
		mvc.perform(get("/stores/nearest/?lng=12345&lat=5432")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk())
			      .andExpect(jsonPath("$", hasSize(2)))
			      .andExpect(jsonPath("$[0].addressName", is(store1.getAddressName())))
			      .andExpect(jsonPath("$[1].addressName", is(store2.getAddressName())));	      
	}
	
	@Test
	public void findFavoriteStores_NoStores_ReturnsBadRequest() throws Exception {
		// verify that the controller response has the mocked stores
		mvc.perform(get("/stores/favorite/")
			      .contentType(MediaType.APPLICATION_JSON))
				  .andExpect(status().isBadRequest());	      
	}
	
	@Test
	public void findFavoriteStores_ValidStores_ReturnsOk() throws Exception {
		// declare two valid stores
		Store store1 = new Store();	
		store1.setAddressName("CITY_1");
		Store store2 = new Store();	
		store2.setAddressName("CITY_2");
		
		// mock the service response
		List<Store> allStores = Arrays.asList(store1,store2);	
		given(service.findFavoriteStores(anyList(), anyList())).willReturn(allStores);
		
		// verify that the controller response has the mocked stores
		mvc.perform(get("/stores/favorite/?storeIds=1")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk())
			      .andExpect(jsonPath("$", hasSize(2)))
			      .andExpect(jsonPath("$[0].addressName", is(store1.getAddressName())))
			      .andExpect(jsonPath("$[1].addressName", is(store2.getAddressName())));	      
	}
	
	@Test
	public void findNearestFavoriteStores_NoStores_ReturnsBadRequest() throws Exception {
		// verify that the controller response has the mocked stores
		mvc.perform(get("/stores/favorite/nearest/?lng=12345&lat=54321")
			      .contentType(MediaType.APPLICATION_JSON))
				  .andExpect(status().isBadRequest());	      
	}
	
	@Test
	public void findNearestFavoriteStores_NoLatLng_ReturnsBadRequest() throws Exception {
		// verify that the controller response has the mocked stores
		mvc.perform(get("/stores/favorite/nearest/?storeIds=1")
			      .contentType(MediaType.APPLICATION_JSON))
				  .andExpect(status().isBadRequest());	      
	}
	
	@Test
	public void findNearestFavoriteStores_ValidStores_ReturnsOk() throws Exception {
		// declare two valid stores
		Store store1 = new Store();	
		store1.setAddressName("CITY_1");
		Store store2 = new Store();	
		store2.setAddressName("CITY_2");
		
		// mock the service response
		List<Store> allStores = Arrays.asList(store1,store2);	
		given(service.findNearestFavoriteStores(anyDouble(), anyDouble(), anyList(), anyList())).willReturn(allStores);
		
		// verify that the controller response has the mocked stores
		mvc.perform(get("/stores/favorite/nearest/?lng=12345&lat=54321&storeIds=1")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk())
			      .andExpect(jsonPath("$", hasSize(2)))
			      .andExpect(jsonPath("$[0].addressName", is(store1.getAddressName())))
			      .andExpect(jsonPath("$[1].addressName", is(store2.getAddressName())));	      
	}
	
	@Test
	public void findAllStores_ReturnsCacheHeader() throws Exception {
		// declare two valid stores
		Store store1 = new Store();	
		store1.setAddressName("CITY_1");
		Store store2 = new Store();	
		store2.setAddressName("CITY_2");
		
		// mock the service response
		List<Store> allStores = Arrays.asList(store1,store2);	
		given(service.findAllStores(anyList(), anyInt())).willReturn(allStores);
		
		// verify that the controller response has the mocked stores
		mvc.perform(get("/stores/")
			      .contentType(MediaType.APPLICATION_JSON))
				  .andExpect(header().string("Cache-Control", "max-age=3600, must-revalidate, no-transform"));	
	}
	
	@Test
	public void findNearestStores_ReturnsCacheHeader() throws Exception {
		// declare two valid stores
		Store store1 = new Store();	
		store1.setAddressName("CITY_1");
		Store store2 = new Store();	
		store2.setAddressName("CITY_2");
		
		// mock the service response
		List<Store> allStores = Arrays.asList(store1,store2);	
		given(service.findNearestStores(anyDouble(), anyDouble(), anyList(), anyInt())).willReturn(allStores);
		
		// verify that the controller response has the mocked stores
		mvc.perform(get("/stores/nearest/?lng=12345&lat=5432")
			      .contentType(MediaType.APPLICATION_JSON))
				  .andExpect(header().string("Cache-Control", "max-age=3600, must-revalidate, no-transform"));
	}
	
	@Test
	public void findFavoriteStores_ReturnsNoCacheHeader() throws Exception {
		// declare two valid stores
		Store store1 = new Store();	
		store1.setAddressName("CITY_1");
		Store store2 = new Store();	
		store2.setAddressName("CITY_2");
		
		// mock the service response
		List<Store> allStores = Arrays.asList(store1,store2);	
		given(service.findFavoriteStores(anyList(), anyList())).willReturn(allStores);
		
		// verify that the controller response has the mocked stores
		mvc.perform(get("/stores/favorite/?storeIds=1")
			    .contentType(MediaType.APPLICATION_JSON))
				.andExpect(header().doesNotExist("Cache-Control"));    
	}
	
	@Test
	public void findNearestFavoriteStores_ReturnsNoCacheHeader() throws Exception {
		// declare two valid stores
		Store store1 = new Store();	
		store1.setAddressName("CITY_1");
		Store store2 = new Store();	
		store2.setAddressName("CITY_2");
		
		// mock the service response
		List<Store> allStores = Arrays.asList(store1,store2);	
		given(service.findNearestFavoriteStores(anyDouble(), anyDouble(), anyList(), anyList())).willReturn(allStores);
		
		// verify that the controller response has the mocked stores
		mvc.perform(get("/stores/favorite/nearest/?lng=12345&lat=54321&storeIds=1")
			      .contentType(MediaType.APPLICATION_JSON))
				  .andExpect(header().doesNotExist("Cache-Control"));  
	}
}
