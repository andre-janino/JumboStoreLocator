package com.jumbo.storeservice.service;

import java.util.List;

import com.jumbo.storeservice.entity.Store;

/**
 * Service interface for the Store document, mostly use to fetch filtered store lists.
 *  
 * @author André Janino
 */
public interface StoreService {
	
	public List<Store> findAllStores(List<String> storeTypes);
	public List<Store> findNearestStores(Double lng, Double lat, List<String> storeTypes, int limit);
}
