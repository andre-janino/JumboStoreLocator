package com.jumbo.storeservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * This document contains a relation of users and their favorite stores
 * 
 * @author André Janino
 */
@Data
@Document(collection = "favorites")
public class Favorite {
	
	@Id
    private String id;
	
	private String userName;
	
	private String storeId;
	
	public Favorite() {}
	
	public Favorite(String userName, String storeId) {
		this.userName = userName;
		this.storeId = storeId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
}
