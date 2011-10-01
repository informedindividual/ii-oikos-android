/**
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; version 2 of the License. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY. See the GNU General Public License for more details. Copyright (C) 2011 Oikos.no, developed
 * by InformedIndividual.org & CellObject.com
 */
package com.cellobject.oikos.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import android.content.res.XmlResourceParser;
import com.cellobject.oikos.model.County;
import com.cellobject.oikos.model.Store;

public class XmlFileParser {
	private static final String XML_TAG_ATTRIBUTE_ID = "id";

	private static final String XML_TAG_ATTRIBUTE_NAME = "name";

	private static final String XML_TAG_ATTRIBUTE_LOGO_URL = "logoUrl";

	private static final String XML_TAG_ATTRIBUTE_LOGO = "logo";

	private static final String XML_TAG_STORE = "store";

	private static final String XML_TAG_COUNTY = "county";

	//private static final String XML_TAG_PLACE = "place";
	public XmlFileParser() {
		super();
	}

	public List<Store> parseStoresXml(final String storesXml) throws XmlPullParserException, IOException {
		final List<Store> stores = new ArrayList<Store>();
		final XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		final XmlPullParser xmlParser = factory.newPullParser();
		final InputStream xmlInputStream = new ByteArrayInputStream(storesXml.getBytes("UTF-8"));
		final Reader xmlReader = new InputStreamReader(xmlInputStream);
		xmlParser.setInput(xmlReader);
		int eventType = -1;
		while (eventType != XmlResourceParser.END_DOCUMENT) {
			if (eventType == XmlResourceParser.START_TAG) {
				final String tagName = xmlParser.getName();
				if (tagName.equals(XML_TAG_STORE)) {
					final String storeId = xmlParser.getAttributeValue(null, XML_TAG_ATTRIBUTE_ID).trim();
					final String storeName = xmlParser.getAttributeValue(null, XML_TAG_ATTRIBUTE_NAME).trim();
					final String storeLogoUrl = xmlParser.getAttributeValue(null, XML_TAG_ATTRIBUTE_LOGO_URL).trim();
					final String storeLogo = xmlParser.getAttributeValue(null, XML_TAG_ATTRIBUTE_LOGO).trim();
					stores.add(new Store(storeId, storeName, storeLogoUrl, storeLogo));
				}
			}
			eventType = xmlParser.next();
		}
		return stores;
	}

	public List<County> parseCountiesXml(final String countiesXml) throws XmlPullParserException, IOException {
		final List<County> counties = new ArrayList<County>();
		final XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		final XmlPullParser xmlParser = factory.newPullParser();
		final InputStream xmlInputStream = new ByteArrayInputStream(countiesXml.getBytes("UTF-8"));
		final Reader xmlReader = new InputStreamReader(xmlInputStream);
		xmlParser.setInput(xmlReader);
		int eventType = -1;
		while (eventType != XmlResourceParser.END_DOCUMENT) {
			if (eventType == XmlResourceParser.START_TAG) {
				final String tagName = xmlParser.getName();
				if (tagName.equals(XML_TAG_COUNTY)) {
					final String countyId = xmlParser.getAttributeValue(null, XML_TAG_ATTRIBUTE_ID).trim();
					final String countyName = xmlParser.getAttributeValue(null, XML_TAG_ATTRIBUTE_NAME).trim();
					counties.add(new County(countyId, countyName));
				}
			}
			eventType = xmlParser.next();
		}
		return counties;
	}
}
