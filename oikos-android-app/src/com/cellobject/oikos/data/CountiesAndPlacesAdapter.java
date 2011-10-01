/**
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; version 2 of the License. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY. See the GNU General Public License for more details. Copyright (C) 2011 Oikos.no, developed
 * by InformedIndividual.org & CellObject.com
 */
package com.cellobject.oikos.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class CountiesAndPlacesAdapter {
	// Database fields
	public static final String KEY_ROW_ID = "_id";

	public static final String KEY_NAME = "name";

	public static final String KEY_COUNTY_ID = "county_id";

	public static final String TABLE_COUNTIES = "counties";

	public static final String TABLE_PLACES = "places";

	private SQLiteDatabase database;

	private final CountiesAndPlacesDatabaseHelper countiesAndPlacesDatabaseHelper;

	public CountiesAndPlacesAdapter(final Context context) {
		countiesAndPlacesDatabaseHelper = new CountiesAndPlacesDatabaseHelper(context);
	}

	public CountiesAndPlacesAdapter openReadableDatabase() throws SQLException {
		database = countiesAndPlacesDatabaseHelper.getReadableDatabase();
		return this;
	}

	public CountiesAndPlacesAdapter openWritableDatabase() throws SQLException {
		database = countiesAndPlacesDatabaseHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		countiesAndPlacesDatabaseHelper.close();
	}

	private ContentValues createCountyContentValues(final String county) {
		final ContentValues values = new ContentValues();
		values.put(KEY_NAME, county);
		return values;
	}

	private ContentValues createPlaceContentValues(final long countyId, final String place) {
		final ContentValues values = new ContentValues();
		values.put(KEY_NAME, place);
		values.put(KEY_COUNTY_ID, countyId);
		return values;
	}

	//************************ START: COUNTIES *****************************************
	/**
	 * Create a new county. If the county is successfully created return the new rowId for that one, otherwise return a -1 to
	 * indicate failure.
	 */
	public long createCounty(final String county) {
		final ContentValues initialValues = createCountyContentValues(county);
		return database.insert(TABLE_COUNTIES, null, initialValues);
	}

	/**
	 * Update the county
	 */
	public boolean updateCounty(final long rowId, final String county) {
		final ContentValues updateValues = createCountyContentValues(county);
		return database.update(TABLE_COUNTIES, updateValues, KEY_ROW_ID + "=" + rowId, null) > 0;
	}

	/**
	 * Delete county
	 */
	public boolean deleteCounty(final long rowId) {
		return database.delete(TABLE_COUNTIES, KEY_ROW_ID + "=" + rowId, null) > 0;
	}

	/**
	 * Return a cursor over the list of all counties in the database.
	 */
	public Cursor fetchAllCounties() {
		return database.query(TABLE_COUNTIES, new String[]{KEY_ROW_ID, KEY_NAME}, null, null, null, null, null);
	}

	/**
	 * Return a cursor positioned at the defined county.
	 */
	public Cursor fetchCounty(final long rowId) throws SQLException {
		final Cursor cursor = database.query(true, TABLE_COUNTIES, new String[]{KEY_ROW_ID, KEY_NAME}, KEY_ROW_ID + "=" + rowId, null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	//************************ END: COUNTIES *****************************************
	//************************ START: PLACES *****************************************
	/**
	 * Create a new place. If the place is successfully created return the new rowId for that place, otherwise return a -1 to
	 * indicate failure.
	 */
	public long createPlace(final long countyId, final String place) {
		final ContentValues initialValues = createPlaceContentValues(countyId, place);
		return database.insert(TABLE_PLACES, null, initialValues);
	}

	/**
	 * Update the place.
	 */
	public boolean updatePlace(final long rowId, final String place) {
		final ContentValues updateValues = createCountyContentValues(place);
		return database.update(TABLE_PLACES, updateValues, KEY_ROW_ID + "=" + rowId, null) > 0;
	}

	/**
	 * Delete place.
	 */
	public boolean deletePlace(final long rowId) {
		return database.delete(TABLE_PLACES, KEY_ROW_ID + "=" + rowId, null) > 0;
	}

	/**
	 * Return a cursor over the list of all places in a county in the database.
	 */
	public Cursor fetchPlaces(final long countyId) {
		final String whereStatement = "(" + KEY_COUNTY_ID + "=" + countyId + ")";
		return database.query(TABLE_PLACES, new String[]{KEY_ROW_ID, KEY_NAME}, whereStatement, null, null, null, null);
	}

	/**
	 * Return a cursor positioned at the defined place.
	 */
	public Cursor fetchPlace(final long rowId) throws SQLException {
		final Cursor cursor = database.query(true, TABLE_PLACES, new String[]{KEY_ROW_ID, KEY_NAME}, KEY_ROW_ID + "=" + rowId, null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}
	//************************ END: PLACES *****************************************
}
