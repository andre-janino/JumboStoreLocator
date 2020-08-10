package com.jumbo.storeservice.service;

import java.util.List;

import com.jumbo.storeservice.entity.Favorite;

/**
 * Service interface for the Favorite object. 
 * 
 * @author André Janino
 */
public interface FavoriteService {

	public List<Favorite> findAllFavorites(String userName);
	public Favorite addFavorite(String userName, String mapId);
	public boolean removeFavorite(String userName, String mapId);
}
