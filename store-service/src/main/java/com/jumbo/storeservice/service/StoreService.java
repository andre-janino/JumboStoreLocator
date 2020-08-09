package com.jumbo.storeservice.service;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.jumbo.storeservice.entity.Store;

/**
 * Service interface for the Store document, mostly use to fetch filtered store lists.
 *  
 * @author André Janino
 */
public interface StoreService {
	
	public List<Store> findAllStores();
	public List<Store> findNearestStores(@PathVariable("lng") Double lng, @PathVariable("lat") Double lat, int limit);
	public List<Store> create(@RequestBody Iterable<Store> store);
}
