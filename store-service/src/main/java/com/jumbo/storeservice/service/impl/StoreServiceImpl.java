package com.jumbo.storeservice.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
	 * Return all existing stores.
	 */
	@Override
	public List<Store> findAll() {
		log.info("Find all stores called.");
		Iterable<Store> stores = repository.findAll();
		ArrayList<Store> result = Lists.newArrayList(stores);
		log.info("Found " + result.size() + " store(s), returning.");
		return result;
	}
	
	/**
	 * Return nearest 5 stores.
	 */
	@Override
	public List<Store> findNearestStores() {
		log.info("Find nearest stores called.");
		Page<Store> stores = repository.findAll(PageRequest.of(1, 5));
		ArrayList<Store> result = Lists.newArrayList(stores);
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
	public Store create(Store store) {
		log.info("Create store called.");
		Store createdStore = repository.save(store);
		log.info("Store created successfully.");
		return createdStore;
	}
}