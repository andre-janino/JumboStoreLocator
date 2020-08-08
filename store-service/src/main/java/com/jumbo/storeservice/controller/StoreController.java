package com.jumbo.storeservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jumbo.storeservice.entity.Store;
import com.jumbo.storeservice.service.StoreService;

/**
 * Request controller for the store controller.
 * 
 * Bad requests (BadRequest) and resources not found (ResourceNotFound) errors are handled on UserService.
 * 
 * @author André Janino
 */
@RestController
public class StoreController {

	private StoreService service;
	
	public StoreController(StoreService service) {
		this.service = service;
	}
	
	/**
	 * This method reaches out to the user service and return a json object containing a list of all stores.
	 * 
	 * @return a list of all stores
	 */
	@GetMapping("/stores")
	public List<Store> retrieveAllUsers(){
		return service.findAll();
	}
	
	/**
	 * This method reaches out to the user service and return a json object containing 5 stores
	 * 
	 * @return a list of the 5 nearest stores
	 */
	@GetMapping("/stores/nearest")
	public List<Store> retrieveUser() {
		return service.findNearestStores();
	}
	
	/**
	 * This method reaches out to the store service and creates a new store.
	 * 
	 * @param store object to be created
	 * @return the created store object, or an error response
	 */
	@PostMapping("/stores")
	public Store createUser(@RequestBody Store store) {
		return service.create(store);
	}
}
