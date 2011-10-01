package com.cellobject.oikos.model;

public class RequestWrapper {
	private String productName = "";

	private String storeId = "";

	private String store = "";

	private String countyId = "";

	private String county = "";

	private String placeId = "";

	private String place = "";

	private String storeFacebookPageId = "";

	public RequestWrapper(final String productName, final String storeId, final String store) {
		this.productName = productName;
		this.storeId = storeId;
		this.store = store;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(final String productName) {
		this.productName = productName;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(final String storeId) {
		this.storeId = storeId;
	}

	public String getStore() {
		return store;
	}

	public void setStore(final String store) {
		this.store = store;
	}

	public String getCountyId() {
		return countyId;
	}

	public void setCountyId(final String countyId) {
		this.countyId = countyId;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(final String county) {
		this.county = county;
	}

	public String getPlaceId() {
		return placeId;
	}

	public void setPlaceId(final String placeId) {
		this.placeId = placeId;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(final String place) {
		this.place = place;
	}

	public String getStoreFacebookPageId() {
		return storeFacebookPageId;
	}

	public void setStoreFacebookPageId(final String storeFacebookPageId) {
		this.storeFacebookPageId = storeFacebookPageId;
	}
}
