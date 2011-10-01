/**
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; version 2 of the License. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY. See the GNU General Public License for more details. Copyright (C) 2011 Oikos.no, developed
 * by InformedIndividual.org & CellObject.com
 */
package com.cellobject.oikos.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CountiesAndPlacesDatabaseHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "oikos";

	private static final int DATABASE_VERSION = 1;

	// Database creation SQL statement
	private static final String CREATE_COUNTIES_SQL = "CREATE TABLE " + CountiesAndPlacesAdapter.TABLE_COUNTIES + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
	+ CountiesAndPlacesAdapter.KEY_NAME + " TEXT NOT NULL);";

	private static final String CREATE_PLACES_SQL = "CREATE TABLE " + CountiesAndPlacesAdapter.TABLE_PLACES + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
	+ CountiesAndPlacesAdapter.KEY_NAME + " TEXT NOT NULL, " + CountiesAndPlacesAdapter.KEY_COUNTY_ID + " INTEGER NOT NULL, FOREIGN KEY("
	+ CountiesAndPlacesAdapter.KEY_COUNTY_ID + ") REFERENCES " + CountiesAndPlacesAdapter.TABLE_COUNTIES + "(_id));";

	// Database drop SQL statement
	private static final String DROP_COUNTIES_SQL = "DROP TABLE IF EXISTS " + CountiesAndPlacesAdapter.TABLE_COUNTIES + ";";

	private static final String DROP_PLACES_SQL = "DROP TABLE IF EXISTS " + CountiesAndPlacesAdapter.TABLE_PLACES + ";";

	/**
	 * The database is created when the constructor is called.
	 * 
	 * @param context
	 */
	public CountiesAndPlacesDatabaseHelper(final Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Method is called during creation of the database
	 */
	@Override
	public void onCreate(final SQLiteDatabase db) {
		db.execSQL(CREATE_COUNTIES_SQL);
		db.execSQL(CREATE_PLACES_SQL);
	}

	/**
	 * Method is called during an update of the database, e.g. if you increase the database version
	 */
	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		Log.w(CountiesAndPlacesDatabaseHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL(DROP_PLACES_SQL);//run this first because of foreign key constraints
		db.execSQL(DROP_COUNTIES_SQL);
		onCreate(db);
	}
}
