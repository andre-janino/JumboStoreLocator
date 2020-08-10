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
 * Service implementation for the Store document. Managing stores (CRUDE) is not supported in this service.
 * 
 * @author André Janino
 */
@Service
public class StoreServiceImpl implements StoreService {
	
	@Autowired
	private StoreRepository repository;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Return all stores, filtered by store types.
	 */
	@Override
	public List<Store> findAllStores(List<String> storeTypes) {
		log.info("Find all stores called.");
		List<Store> stores = repository.findByLocationTypeIn(storeTypes);
		ArrayList<Store> result = Lists.newArrayList(stores);
		log.info("Found " + result.size() + " store(s), returning.");
		return result;
	}
	
	/**
	 * Return nearest N stores to the provided lat/lng, filtered by store types.
	 */
	@Override
	public List<Store> findNearestStores(Double lng, Double lat, List<String> storeTypes, int limit) {
		log.info("Find all stores called.");		
		List<Store> result = repository.findNearestStores(lng, lat, storeTypes, PageRequest.of(0, limit));
		log.info("Found " + result.size() + " store(s), returning.");
		return result;
	}
}