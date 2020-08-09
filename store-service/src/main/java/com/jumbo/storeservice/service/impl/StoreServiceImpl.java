package com.jumbo.storeservice.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.jumbo.storeservice.entity.Store;
import com.jumbo.storeservice.repository.StoreRepository;
import com.jumbo.storeservice.service.StoreService;

/**
 * Service implementation for the Store document.
 * 
 * Returns errors (BadRequest and ResourceNotFound) in case the rest command was malformed or no user was found, respectively.
 * 
 * @author André Janino
 */
@Service
public class StoreServiceImpl implements StoreService {
	
	@Autowired
	private StoreRepository repository;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Return all stores
	 */
	@Override
	public List<Store> findAllStores() {
		log.info("Find all stores called.");
		List<Store> stores = repository.findAll();
		ArrayList<Store> result = Lists.newArrayList(stores);
		log.info("Found " + result.size() + " store(s), returning.");
		return result;
	}
	
	/**
	 * Return nearest N stores.
	 */
	@Override
	public List<Store> findNearestStores(Double lng, Double lat, int limit) {
		log.info("Find all stores called.");
		log.info(lng + " - " + lat + " | " + limit);
		
		List<Store> result = repository.findNearestStores(lng, lat, PageRequest.of(0, limit));
		log.info("Found " + result.size() + " store(s), returning.");
		return result;
	}
	
	/**
	 * Create a store.
	 * 
	 * @param store A store object that was serialized from a json object.
	 * @return the created store object
	 */
	@Override
	public List<Store> create(Iterable<Store> stores) {
		log.info("Create store called.");
		Iterable<Store> createdStores = repository.saveAll(stores);
		log.info("Stores created successfully");
		return  Lists.newArrayList(createdStores);
	}
}