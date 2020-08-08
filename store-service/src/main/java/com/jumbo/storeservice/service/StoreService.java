package com.jumbo.storeservice.service;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;

import com.jumbo.storeservice.entity.Store;

/**
 * Service interface for the Store document, mostly use to fetch filtered store lists.
 *  
 * @author André Janino
 */
public interface StoreService {
	
	public List<Store> findAll();
	public List<Store> findNearestStores();
	public Store create(@RequestBody Store store);
}
