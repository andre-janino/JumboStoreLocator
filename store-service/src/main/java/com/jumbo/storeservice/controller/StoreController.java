package com.jumbo.storeservice.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jumbo.storeservice.entity.Store;
import com.jumbo.storeservice.service.StoreService;

/**
 * Request controller for the store service.
 * 
 * Both findAllStores and findNearest implement 1hr cache control.
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
	 * If no limit is provided, return by default a maximum of 1000 results.
	 * Results are cached for one hour.
	 * 
	 * @param storeTypes A list of store types
	 * @response The HttpServletResponse object, used to write the cache-control header
	 * @return a list of all stores
	 */
	@GetMapping("/stores/")
	public List<Store> findAllStores(@RequestParam(defaultValue = "SupermarktPuP,PuP,Supermarkt") List<String> storeTypes, @RequestParam(defaultValue = "1000") Integer limit, final HttpServletResponse response) {
		response.addHeader("Cache-Control", "max-age=3600, must-revalidate, no-transform"); // cache find all store results for an hour
		return service.findAllStores(storeTypes, limit);
	}
	
	/**
	 * Returns a list of N nearest stores by type, in respect to a lat/lng for every store type.
	 * If no limit is provided, return by default a maximum of 1000 results.
	 * Results are cached for one hour.
	 *
	 * @param lng
	 * @param lat
	 * @param storeTypes A list of store types
	 * @param limit The max number of stores to be queried
	 * @response The HttpServletResponse object, used to write the cache-control header
	 * @return a list all nearest stores
	 */
	@GetMapping("/stores/nearest")
	public List<Store> findNearestStores(@RequestParam Double lng, @RequestParam Double lat, @RequestParam(defaultValue = "SupermarktPuP,PuP,Supermarkt") List<String> storeTypes, @RequestParam(defaultValue = "1000") Integer limit, final HttpServletResponse response) {
		response.addHeader("Cache-Control", "max-age=3600, must-revalidate, no-transform"); // cache find all store results for an hour
		return service.findNearestStores(lng, lat, storeTypes, limit);
	}
	
	/**
	 * Returns a list of all favorite stores, irespective of location
	 *
	 * @param storeIds A list of store ids
	 * @param storeTypes A list of store types
	 * @return a filtered list of stores stores
	 */
	@GetMapping("/stores/favorite")
	public List<Store> findFavoriteStores(@RequestParam List<String> storeIds, @RequestParam(defaultValue = "SupermarktPuP,PuP,Supermarkt") List<String> storeTypes) {
		return service.findFavoriteStores(storeIds, storeTypes);
	}
	
	/**
	 * Returns a list of all favorite stores, which may be ordered by lat/lng (if provided)
	 *
	 * @param lng Optional longitude
	 * @param lat Optional latitude
	 * @param storeIds A list of store ids
	 * @param storeTypes A list of store types
	 * @return a list of filtered nearest stores
	 */
	@GetMapping("/stores/favorite/nearest")
	public List<Store> findNearestFavoriteStores(@RequestParam Double lng, @RequestParam Double lat, @RequestParam List<String> storeIds, @RequestParam(defaultValue = "SupermarktPuP,PuP,Supermarkt") List<String> storeTypes) {
		return service.findNearestFavoriteStores(lng, lat, storeIds, storeTypes);
	}
}
