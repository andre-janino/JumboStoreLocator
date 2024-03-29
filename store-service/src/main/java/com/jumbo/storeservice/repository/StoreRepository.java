package com.jumbo.storeservice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.jumbo.storeservice.entity.Store;

/**
 * Store repository with custom queries. Requires 2dsphere index on the position column.
 * 
 * @author André Janino
 */
public interface StoreRepository extends MongoRepository <Store, String> {

	Page<Store> findAll(Pageable pageable);

	/**
	 * Query all stores by locationType, useful if the user has not yet provided a location but wants to checkout only pick-up points, for example.
	 * 
	 * @param locationType SupermarktPuP, PuP or Supermarkt. 
	 * @return A list of stores that match the provided location type
	 */
    List<Store> findByLocationTypeIn(List<String> locationType, Pageable pageable);
    
    /**
	 * Query all stores by locationType and sapStoreId, useful if the user has not yet provided a location but wants to query his favorite stores (and filter them by type, possibly)
	 * 
	 * @param locationType SupermarktPuP, PuP or Supermarkt.
	 * @param sapStoreId A list of sapStoreIds  
	 * @return A list of stores that match the provided location type and sapStoreIds
	 */
    List<Store> findByLocationTypeInAndSapStoreIDIn(List<String> locationType, List<String> sapStoreId);
    
	/**
	 * Performs a geospatial query based on provided longitude/latitudes: https://docs.mongodb.com/manual/reference/operator/aggregation/geoNear/
	 * 
	 * Hardcoding max distance to 312000m (as the Netherlands extends 312km from north to south)
	 * The maxDistance parameter is optional though, and could be easily removed if we want to allow for a wider search range.
	 * 
	 * That being said, in a multi-country company it would be a good idea to store the max distance of each country and work with that,
	 * as it usually makes little sense for a customer to search for stores in multiple countries at once (but nothing would stop said user
	 * to query in a different country, of course). 
	 * 
	 * It is important to note that $limit could have been employed on the aggregation (allowing us to define the 5 nearest stores query),
	 * but employing Pageable object allows us to make use of the same query method.
	 * 
	 * @param longitude
	 * @param latitude
	 * @return A list of stores, ordered by their distance to the provided location
	 */
	@Aggregation(value =
			"{" +
			"  \"$geoNear\": {" +
			"     \"near\": { " +
			"         type : \"Point\" ,\n" +
			"         coordinates : [ ?0, ?1] " +
			"      },\n" +
			"      maxDistance: 312000," +
			"      key: position," +
			"      distanceField: distance," + // distance is calculated in meters
			"      query: { \"locationType\": {$in: ?2} }" +
			"   }\n" +
			"}")
    List<Store> findNearestStores(Double longitude, Double latitude, List<String> locationType, Pageable pageable);
	
	/**
	 * Similar to findNearestStores, but also takes into account a list of store IDs. 
	 * Although this query is flexible, it is currently employed to retrieve a list of favorited stores in this implementation.
	 * 
	 * Returns a list of stores 
	 * @param longitude
	 * @param latitude
	 * @param locationType
	 * @param pageable
	 * @return A list of stores, filtered by id and ordered by their distance to the provided location
	 */
	@Aggregation(value =
			"{" +
			"  \"$geoNear\": {" +
			"     \"near\": { " +
			"         type : \"Point\" ,\n" +
			"         coordinates : [ ?0, ?1] " +
			"      },\n" +
			"      maxDistance: 312000," +
			"      key: position," +
			"      distanceField: distance," + // distance is calculated in meters
			"      query: { \"locationType\": {$in: ?2}, \"sapStoreID\": {$in: ?3} }" +
			"   }\n" +
			"}")
    List<Store> findNearestStoresById(Double longitude, Double latitude, List<String> locationType, List<String> sapStoreId);
}

