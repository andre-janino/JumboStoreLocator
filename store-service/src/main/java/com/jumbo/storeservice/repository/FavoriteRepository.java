package com.jumbo.storeservice.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.jumbo.storeservice.entity.Favorite;

/**
 * Favorite repository, storing a relation of users and stores
 * 
 * @author André Janino
 */
public interface FavoriteRepository extends MongoRepository <Favorite, String> {

	/**
	 * Queries all favorited stores for a user
	 * 
	 * @param user
	 * @return A list of favorited store ids
	 */
	List<Favorite> findByUserName(String user);
	
	/**
	 * Find a favorite by userName and storeId.
	 * 
	 * @param userName
	 * @param storeId
	 * @return A favorite object.
	 */
	Favorite findByUserNameAndId(String userName, String storeId);
	
	/**
	 * Delete a store by userName and storeId
	 * 
	 * @param userName
	 * @param storeId
	 */
	void deleteByUserNameAndStoreId(String userName, String storeId);
}
