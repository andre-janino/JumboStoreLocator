package com.jumbo.storeservice.entity;

import java.io.IOException;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * This document contains all the store's information.
 * 
 * @author André Janino
 */
@Data
@Document(collection = "stores")
public class Store {

	@Id
	@JsonIgnore
    private String id;
	
	private String city;
	
	@JsonIgnore
	private String postalCode;
	
	private String street;
	
	private String street2;
	
	@JsonIgnore
	private String street3;
	
	private String addressName;

	private String uuid;

	@JsonSerialize(using = GeoJSONPositionSerializer.class)
	private GeoJsonPoint position;
	
	@JsonIgnore
	private String complexNumber;
	
	private boolean showWarningMessage;
	
	private String todayOpen;
	
	private String locationType;
	
	private boolean collectionPoint;
	
	private String sapStoreID;
	
	private String todayClose;
	
	private String distance;
	
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getStreet2() {
		return street2;
	}

	public void setStreet2(String street2) {
		this.street2 = street2;
	}

	public String getStreet3() {
		return street3;
	}

	public void setStreet3(String street3) {
		this.street3 = street3;
	}

	public String getAddressName() {
		return addressName;
	}

	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Object getPosition() {
		return position;
	}

	public void setPosition(GeoJsonPoint position) {
		this.position = position;
	}

	public String getComplexNumber() {
		return complexNumber;
	}

	public void setComplexNumber(String complexNumber) {
		this.complexNumber = complexNumber;
	}

	public boolean isShowWarningMessage() {
		return showWarningMessage;
	}

	public void setShowWarningMessage(boolean showWarningMessage) {
		this.showWarningMessage = showWarningMessage;
	}

	public String getTodayOpen() {
		return todayOpen;
	}

	public void setTodayOpen(String todayOpen) {
		this.todayOpen = todayOpen;
	}

	public String getLocationType() {
		return locationType;
	}

	public void setLocationType(String locationType) {
		this.locationType = locationType;
	}

	public boolean isCollectionPoint() {
		return collectionPoint;
	}

	public void setCollectionPoint(boolean collectionPoint) {
		this.collectionPoint = collectionPoint;
	}

	public String getSapStoreID() {
		return sapStoreID;
	}

	public void setSapStoreID(String sapStoreID) {
		this.sapStoreID = sapStoreID;
	}

	public String getTodayClose() {
		return todayClose;
	}

	public void setTodayClose(String todayClose) {
		this.todayClose = todayClose;
	}
	
	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	/**
	 * Transforms GeoJSON into a format that is understood by google maps.
	 * 
	 * @author André Janino
	 */
	static class GeoJSONPositionSerializer extends JsonSerializer<GeoJsonPoint> {

        @Override
        public void serialize(GeoJsonPoint value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            gen.writeNumberField("lat", value.getY());
            gen.writeNumberField("lng", value.getX());
            gen.writeEndObject();
        }
    }
}
