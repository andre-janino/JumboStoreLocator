package com.jumbo.storeservice.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jumbo.storeservice.entity.Favorite;
import com.jumbo.storeservice.repository.FavoriteRepository;
import com.jumbo.storeservice.service.FavoriteService;

/**
 * Service implementation for the Favorite document.
 * 
 * @author André Janino
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {

	@Autowired
	private FavoriteRepository repository;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Retrieve all favorited stores of a user.
	 * 
	 * @param userName The user requesting his favorited store info
	 * @return A list of favorited store ids
	 */
	@Override
	public List<Favorite> findAllFavorites(String userName) {
		log.info("Find all favorites called.");
		List<Favorite> favorites = repository.findByUserName(userName);
		log.info("Found " + favorites.size() + " favorite(s), returning.");
		return favorites;
	}

	/**
	 * Favorite a store for a provided user.
	 * 
	 * @param userName The user wanting to favorite a store
	 * @param storeId The target store id
	 * @return The favorited user/store pair object
	 */
	@Override
	public Favorite addFavorite(String userName, String storeId) {
		log.info("Add favorites called.");
		Favorite favorite = new Favorite(userName, storeId);
		repository.save(favorite);
		log.info("Favorite added.");
		return null;
	}

	/**
	 * Unfavorite a store for a given user.
	 * 
	 * @param userName The user wanting to unfavorite a store
	 * @param storeId The target store id
	 * @return true if all goes well (otherwise, it will throw an exception before completing)
	 */
	@Override
	public boolean removeFavorite(String userName, String storeId) {
		log.info("Remove favorite called.");
		repository.deleteByUserNameAndStoreId(userName, storeId);
		log.info("Favorite removed.");
		return true;
	}
}
