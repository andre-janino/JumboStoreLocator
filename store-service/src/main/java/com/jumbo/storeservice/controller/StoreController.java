package com.jumbo.storeservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
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
	 * Return a list containing all stores by type, irrespective to the current position
	 * 
	 * If no limit is provided, return by default a maximum of 1000 results.
	 */
	@GetMapping("/stores/")
	public List<Store> findAllStores(@RequestParam List<String> storeTypes) {
		return service.findAllStores(storeTypes);
	}
	
	/**
	 * Returns list of N nearest stores by type, in respect to a lat/lng for every store type.
	 * 
	 * If no limit is provided, return by default a maximum of 1000 results.
	 *
	 * @return a list of all stores
	 */
	@GetMapping("/stores/nearest")
	public List<Store> findNearestStores(@RequestParam Double lng, @RequestParam Double lat, @RequestParam List<String> storeTypes, @RequestParam(defaultValue = "1000") Integer limit) {
		return service.findNearestStores(lng, lat, storeTypes, limit);
	}
}
