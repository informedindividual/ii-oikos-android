package com.cellobject.oikos;

import java.util.ArrayList;
import java.util.List;
import android.app.Application;
import com.cellobject.oikos.model.County;
import com.cellobject.oikos.model.Store;
import com.cellobject.oikos.util.HttpWrapper;

public class OikosApplication extends Application {
	private HttpWrapper network = new HttpWrapper();

	private List<Store> stores = new ArrayList<Store>();

	private List<County> counties = new ArrayList<County>();

	public HttpWrapper getNetwork() {
		return network;
	}

	public void setNetwork(final HttpWrapper network) {
		this.network = network;
	}

	public List<Store> getStores() {
		return stores;
	}

	public void setStores(final List<Store> stores) {
		this.stores = stores;
	}

	public List<County> getCounties() {
		return counties;
	}

	public void setCounties(final List<County> counties) {
		this.counties = counties;
	}
}
