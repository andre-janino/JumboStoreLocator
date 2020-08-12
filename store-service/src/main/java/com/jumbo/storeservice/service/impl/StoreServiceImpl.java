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
	 * 
	 * @param storeTypes A list of store types
	 * @return a list of all stores
	 */
	@Override
	public List<Store> findAllStores(List<String> storeTypes, int limit) {
		log.info("Find all stores called.");
		List<Store> stores = repository.findByLocationTypeIn(storeTypes, PageRequest.of(0, limit));
		ArrayList<Store> result = Lists.newArrayList(stores);
		log.info("Found " + result.size() + " store(s), returning.");
		return result;
	}
	
	/**
	 * Return nearest N stores to the provided lat/lng, filtered by store types.
	 * 
	 * @param lng
	 * @param lat
	 * @param storeTypes A list of store types
	 * @param limit The max number of stores to be queried
	 * @return a list all nearest stores
	 */
	@Override
	public List<Store> findNearestStores(Double lng, Double lat, List<String> storeTypes, int limit) {
		log.info("Find all stores called.");		
		List<Store> result = repository.findNearestStores(lng, lat, storeTypes, PageRequest.of(0, limit));
		log.info("Found " + result.size() + " store(s), returning.");
		return result;
	}
	
	/**
	 * Return a list of stores irrespective of location, filtered by store IDs and store types.
	 * 
	 * @param storeIds A list of store ids
	 * @param storeTypes A list of store types
	 * @return a filtered list of stores stores
	 */
	@Override
	public List<Store> findFavoriteStores(List<String> storeIds, List<String> storeTypes) {
		log.info("Find favorite stores called.");	
		List<Store> result = repository.findByLocationTypeInAndSapStoreIDIn(storeTypes, storeIds);
		log.info("Found " + result.size() + " store(s), returning.");
		return result;
	}

	/**
	 * Return nearest N stores to the provided lat/lng, filtered by store IDs and store types.
	 * 
	 * @param lng Optional longitude
	 * @param lat Optional latitude
	 * @param storeIds A list of store ids
	 * @param storeTypes A list of store types
	 * @return a list of filtered nearest stores
	 */
	@Override
	public List<Store> findNearestFavoriteStores(Double lng, Double lat, List<String> storeIds, List<String> storeTypes) {
		log.info("Find nearest favorite stores called.");				
		List<Store> result = repository.findNearestStoresById(lng, lat, storeTypes, storeIds);
		log.info("Found " + result.size() + " store(s), returning.");
		return result;
	}
}