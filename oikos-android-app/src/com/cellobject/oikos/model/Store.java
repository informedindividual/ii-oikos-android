package com.cellobject.oikos.model;

public class Store {
	private String id;

	private String name;

	private String logoUrl;

	private String logo;

	private String facebookPageId = "";

	public Store(final String id, final String name, final String logoUrl, final String logo) {
		super();
		this.id = id;
		this.name = name;
		this.logoUrl = logoUrl;
		this.logo = logo;
		setFacebookPageId(name);
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(final String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(final String logo) {
		this.logo = logo;
	}

	public String getFacebookPageId() {
		return facebookPageId;
	}

	public void setFacebookPageId(final String name) {
		for (final StoreFacebookPage storeFacebookPage : StoreFacebookPage.values()) {
			if (storeFacebookPage.getStore().equalsIgnoreCase(name)) {
				this.facebookPageId = storeFacebookPage.getFacebookPageId();
				break;
			}
		}
	}

	@Override
	public String toString() {
		return getName();
	}
}
