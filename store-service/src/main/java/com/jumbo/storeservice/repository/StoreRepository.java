package com.jumbo.storeservice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.jumbo.storeservice.entity.Store;

/**
 * Store repository with custom queries. Requires 2dsphere index on the position column.
 * 
 * @author André Janino
 */
public interface StoreRepository extends MongoRepository <Store, String> {

	List<Store> findAll();
	
	Page<Store> findAll(Pageable pageable);

	/**
	 * Queries by locationType. Probably has little use as is for this particular application, needs to be used in conjunction with $near
	 * 
	 * @param locationType SupermarktPuP, PuP or Supermarkt
	 * @return A list of stores that match the provided location type
	 */
	@Query("{locationType:'?0'}")
    List<Store> findByLocationType(String locationType);
	
	/**
	 * Performs a geospatial query based on provided longitude/latitudes.
	 * 
	 * Hardcoding max distance to 156000m (as the Netherlands extends 312km from north to south, so we have 1000 * 312/2 = 156000)
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
			"      key: position," +
			"      distanceField: distance," + // distance is calculated in meters
			"   }\n" +
			"}")
    public List<Store> findNearestStores(double longitude, double latitude, Pageable pageable);
}

