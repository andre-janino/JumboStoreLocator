package com.jumbo.storeservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
	 * Return a list containing all stores, irrespective to the current position
	 */
	@GetMapping("/stores/")
	public List<Store> findAllStores() {
		return service.findAllStores();
	}
	
	
	/**
	 * Returns list of N nearest stores in respect to a lat/lng for every store type.
	 * 
	 * If no limit is provided, return by default a maximum of 1000 results (more than enough for this example)
	 *
	 * @return a list of all stores
	 */
	@GetMapping("/stores/nearest")
	public List<Store> findNearestStores(@RequestParam Double lng, @RequestParam Double lat, @RequestParam(defaultValue = "1000") Integer limit) {
		return service.findNearestStores(lng, lat, limit);
	}
	
	/**
	 * Temp method to create all stores.
	 * 
	 * @param store object to be created
	 * @return the created store object, or an error response
	 */
	@PostMapping("/stores")
	public List<Store> createStores(@RequestBody Iterable<Store> stores) {
		return service.create(stores);
	}
}
