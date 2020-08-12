package com.jumbo.storeservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jumbo.storeservice.entity.Favorite;
import com.jumbo.storeservice.service.FavoriteService;

/**
 * Controller for the favorite service.
 * 
 * Contrary to StoreController, favorite resources are no cached, as they may change constantly.
 * 
 * @author André Janino
 */
@RestController
public class FavoriteController {

	private FavoriteService service;
	
	public FavoriteController(FavoriteService service) {
		this.service = service;
	}
	
	/**
	 * This method reaches out to the favorite service returns all stores a user has favorited.
	 * 
	 * @param user object to be created
	 * @return the created user object, or an error response
	 */
	@GetMapping("/favorites/")
	public List<Favorite> getFavorites(@RequestParam String userName) {
		return service.findAllFavorites(userName);
	}
	
	/**
	 * This method reaches out to the favorite service and favorites a store for a given user
	 * 
	 * @param user object to be created
	 * @return the created user object, or an error response
	 */
	@PostMapping("/favorites/")
	public Favorite addFavorite(@RequestParam String userName, @RequestParam String storeId) {
		return service.addFavorite(userName, storeId);
	}
	
	/**
	 * This method reaches out to the favorite service and favorites a store for a given user
	 * 
	 * @param user object to be created
	 * @return the created user object, or an error response
	 */
	@DeleteMapping("/favorites/")
	public boolean removeFavorite(@RequestParam String userName, @RequestParam String storeId) {
		return service.removeFavorite(userName, storeId);
	}
}
