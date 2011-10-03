/**
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; version 2 of the License. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY. See the GNU General Public License for more details. Copyright (C) 2011 Oikos.no, developed
 * by InformedIndividual.org & CellObject.com
 */
package com.cellobject.oikos;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.cellobject.oikos.facebook.FacebookConnector;
import com.cellobject.oikos.facebook.SessionEvents;
import com.cellobject.oikos.model.County;
import com.cellobject.oikos.model.Place;
import com.cellobject.oikos.model.RequestWrapper;
import com.cellobject.oikos.model.Store;
import com.cellobject.oikos.util.MessageUtil;

public class FormActivity extends Activity {
	private final String TAG = "oikos";

	private OikosApplication oikosApplication;

	private MessageUtil messageUtil;

	final String URL_SUBMIT_PRODUCT = "https://informedindividual.org/oikos/m/prodReq/add";

	private final String FACEBOOK_APPID = "203117906393490";

	private final String FACEBOOK_PERMISSION = "publish_stream";

	private final Handler mFacebookHandler = new Handler();

	private FacebookConnector facebookConnector;

	final Runnable mUpdateFacebookNotification = new Runnable() {
		public void run() {
		}
	};

	private SharedPreferences oikosPreferences;

	private int lastSelectedShopPosition;

	private int lastSelectedPlacePosition;

	private ImageButton preferencesBtn;

	private ImageButton infoBtn;

	private EditText messageEditText;

	private EditText storeEditText;

	private EditText placeEditText;

	private ImageButton selectCountyBtn;

	private ImageButton selectPlaceBtn;

	private ImageButton selectShopBtn;

	private Spinner countiesSpinner;

	private Spinner placesSpinner;

	private Spinner storesSpinner;

	private Button sendRequestButton;

	//
	public static final String STORE_NOT_SELECTED = "Butikk, ikke valgt";

	public static final String COUNTY_NOT_SELECTED = "Fylke, ikke valgt";

	public static final String PLACE_NOT_SELECTED = "Sted, ikke valgt";

	public static final String UNDEFINED_ID = "";

	public static final String OTHER_SPECIFY = "Annen: spesifiser";

	public final static String RIMI = "Rimi";

	public final static String ICA_SUPER = "Ica Super";

	public final static String ICA_NAER = "Ica Nær";
	
	public final static String ICA_MAXI = "Ica Maxi";

	public final static String REMA_1000 = "Rema 1000";

	public final static String COOP_OBS = "Coop Obs!";

	public final static String COOP_EXTRA = "Coop Extra";

	public final static String COOP_PRIX = "Coop Prix";

	public final static String COOP_MEGA = "Coop Mega";

	public final static String COOP_MARKED = "Coop Marked";

	public final static String MENY = "Meny";

	public final static String KIWI = "Kiwi";

	public final static String JOKER = "Joker";

	public final static String SPAR = "Spar";

	public final static String BUNNPRIS = "Bunnpris";

	private Store[] stores;

	private County[] counties;

	private final Place[] places = {new Place("", PLACE_NOT_SELECTED), new Place("", OTHER_SPECIFY)}; //initialize with default values

	private RequestWrapper requestWrapper;

	private final int SUBMITTING_DIALOG = 1;

	private final int SUBMIT_PRODUCT = 2;

	private final int SUBMIT_SUCCEEDED_MESSAGE = 3;

	Handler formHandler = new Handler() {
		@Override
		public void handleMessage(final Message message) {
			switch (message.what) {
				case SUBMIT_PRODUCT:
					if (requestWrapper != null) {
						try {
							showDialog(SUBMITTING_DIALOG);
							oikosApplication.getNetwork().httpPost(URL_SUBMIT_PRODUCT, getRequestValues(requestWrapper));
							postMessageOnFacebookWall(requestWrapper.getStoreFacebookPageId(), getFacebookMessage());//post message to facebook
							formHandler.sendMessage(Message.obtain(formHandler, SUBMIT_SUCCEEDED_MESSAGE));
						} catch (final Exception e) {
							messageUtil.textToastMedium(getString(R.string.submitting_problem));
						}
					}
					break;
				case SUBMIT_SUCCEEDED_MESSAGE:
					dismissDialog(SUBMITTING_DIALOG);
					messageUtil.textToastMedium(getString(R.string.submitting_succeeded));
					break;
				default:
					break;
			}
		}
	};

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form);
		//
		this.facebookConnector = new FacebookConnector(FACEBOOK_APPID, this, getApplicationContext(), new String[]{FACEBOOK_PERMISSION});
		oikosApplication = (OikosApplication)this.getApplication();
		messageUtil = new MessageUtil(this);
		oikosPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		lastSelectedPlacePosition = oikosPreferences.getInt(OikosPreferenceActivity.SELECTED_PLACE, 0);
		messageEditText = (EditText)findViewById(R.id.edittxt_message);
		initializeStoreSelection();
		initializeCountyAndPlaceSelection();
		initializeSettingsAndInfoButtons();
		initializeSelectCountyButton();
		initializeRequestButton();
	}

	/**
	 * Facebook message format: Jeg ville blitt glad dersom økologisk <produkt> var tilgjengelig på <Butikk> <sted>, <fylke>.
	 */
	private String getFacebookMessage() {
		String facebookWallMessage = "Jeg ville blitt glad dersom økologisk " + requestWrapper.getProductName() + " var tilgjengelig på "
		+ requestWrapper.getStore() + " ";
		if (!requestWrapper.getPlace().equalsIgnoreCase("")) {
			facebookWallMessage = facebookWallMessage + requestWrapper.getPlace();
		}
		if (!requestWrapper.getCounty().equalsIgnoreCase("")) {
			facebookWallMessage = facebookWallMessage + ", " + requestWrapper.getCounty();
		}
		return facebookWallMessage;
	}

	private void initializeCountyAndPlaceSelection() {
		placeEditText = (EditText)findViewById(R.id.edittxt_place);
		placesSpinner = (Spinner)findViewById(R.id.places_spn);
		final PlacesAdapter placesAdapter = new PlacesAdapter(this, R.layout.place_row, places);
		placesSpinner.setAdapter(placesAdapter);
		selectPlaceBtn = (ImageButton)findViewById(R.id.select_place_btn);
		selectPlaceBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View view) {
				placeEditText.setText("");//reset text
				placeEditText.setVisibility(View.GONE);
				placesSpinner.setVisibility(View.VISIBLE);
				placesSpinner.setSelection(0);
				placesSpinner.performClick();
			}
		});
		placesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(final AdapterView<?> parent, final View itemSelected, final int selectedItemPosition, final long selectedId) {
				final Place selectedPlace = (Place)placesSpinner.getSelectedItem();
				if (selectedPlace.getName().equalsIgnoreCase(OTHER_SPECIFY)) {
					placesSpinner.setVisibility(View.GONE);
					placeEditText.setVisibility(View.VISIBLE);
					selectPlaceBtn.setVisibility(View.VISIBLE);
				}
			}

			public void onNothingSelected(final AdapterView<?> view) {
			}
		});
		counties = oikosApplication.getCounties().toArray(new County[oikosApplication.getCounties().size()]);//convert ArrayList to Array
		countiesSpinner = (Spinner)findViewById(R.id.counties_spn);
		final CountiesAdapter countiesAdapter = new CountiesAdapter(this, R.layout.county_row, counties);
		countiesSpinner.setAdapter(countiesAdapter);
		countiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(final AdapterView<?> parent, final View itemSelected, final int selectedItemPosition, final long selectedId) {
				final County selectedCounty = (County)countiesSpinner.getSelectedItem();
				if (selectedCounty.getName().equalsIgnoreCase(COUNTY_NOT_SELECTED)) {
					placesSpinner.setVisibility(View.GONE);
					placeEditText.setVisibility(View.VISIBLE);
					selectPlaceBtn.setVisibility(View.VISIBLE);
				}
			}

			public void onNothingSelected(final AdapterView<?> view) {
			}
		});
		//
		setLastSelectedCounty();
		setLastEnteredPlaceText();
	}

	private void setLastEnteredPlaceText() {
		final boolean rememberLastSelected = oikosPreferences.getBoolean(OikosPreferenceActivity.SAVE_LAST_SELECTED_VALUES_KEY, true);//default is true
		final String lastPlaceText = oikosPreferences.getString(OikosPreferenceActivity.SELECTED_PLACE_EDITTEXT, "");
		if (rememberLastSelected && !lastPlaceText.equalsIgnoreCase("")) {
			placeEditText.setText(lastPlaceText);
			placesSpinner.setVisibility(View.GONE);
			placeEditText.setVisibility(View.VISIBLE);
		}
	}

	private void initializeStoreSelection() {
		storeEditText = (EditText)findViewById(R.id.store_edittxt);
		storesSpinner = (Spinner)findViewById(R.id.shops_spn);
		selectShopBtn = (ImageButton)findViewById(R.id.select_shop_btn);
		stores = oikosApplication.getStores().toArray(new Store[oikosApplication.getStores().size()]);//convert ArrayList to Array
		final StoresAdapter storesAdapter = new StoresAdapter(this, R.layout.store_row, stores);
		storesSpinner.setAdapter(storesAdapter);
		storesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(final AdapterView<?> parent, final View itemSelected, final int selectedItemPosition, final long selectedId) {
				final Store selectedStore = (Store)storesSpinner.getSelectedItem();
				if (selectedStore.getName().equalsIgnoreCase(OTHER_SPECIFY)) {
					storesSpinner.setVisibility(View.GONE);
					storeEditText.setVisibility(View.VISIBLE);
					selectShopBtn.setVisibility(View.VISIBLE);
				}
			}

			public void onNothingSelected(final AdapterView<?> view) {
			}
		});
		selectShopBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View view) {
				storeEditText.setText("");//reset text
				storeEditText.setVisibility(View.GONE);
				storesSpinner.setVisibility(View.VISIBLE);
				storesSpinner.setSelection(0);
				storesSpinner.performClick();
			}
		});
		//
		final boolean rememberLastSelected = oikosPreferences.getBoolean(OikosPreferenceActivity.SAVE_LAST_SELECTED_VALUES_KEY, true);//default is true
		lastSelectedShopPosition = oikosPreferences.getInt(OikosPreferenceActivity.SELECTED_STORE, 0);
		if (rememberLastSelected && (lastSelectedShopPosition > -1)) {
			storesSpinner.setSelection(lastSelectedShopPosition);
		}
		final String lastEnteredStoreText = oikosPreferences.getString(OikosPreferenceActivity.SELECTED_STORE_EDITTEXT, "");
		if (rememberLastSelected && !lastEnteredStoreText.equalsIgnoreCase("")) {
			storeEditText.setText(lastEnteredStoreText);
		}
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		this.facebookConnector.getFacebook().authorizeCallback(requestCode, resultCode, data);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	/**
	 * Avoid the restart and pause when orientation changes
	 */
	@Override
	public void onConfigurationChanged(final Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * For API level 8 or newer. 
	 */
	public Dialog onCreateDialog(final int id, final Bundle args) {
		if (id == SUBMITTING_DIALOG) {
			final ProgressDialog dlg = new ProgressDialog(this);
			dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dlg.setMessage(getText(R.string.submitting));
			dlg.setIndeterminate(true);
			return dlg;
		}
		return null;
	}
	
	/**
	 * For API level lower than 8. 
	 */
	public Dialog onCreateDialog(final int id) {
		if (id == SUBMITTING_DIALOG) {
			final ProgressDialog dlg = new ProgressDialog(this);
			dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dlg.setMessage(getText(R.string.submitting));
			dlg.setIndeterminate(true);
			return dlg;
		}
		return null;
	}

	private void setLastSelectedCounty() {
		final boolean rememberLastSelected = oikosPreferences.getBoolean(OikosPreferenceActivity.SAVE_LAST_SELECTED_VALUES_KEY, true);//default is true
		final int lastSelectedCountyPosition = oikosPreferences.getInt(OikosPreferenceActivity.SELECTED_COUNTY, 0);
		if (rememberLastSelected && (lastSelectedCountyPosition > -1)) {
			countiesSpinner.setSelection(lastSelectedCountyPosition);
		}
	}

	private void setLastSelectedPlace() {
		final boolean rememberLastSelected = oikosPreferences.getBoolean(OikosPreferenceActivity.SAVE_LAST_SELECTED_VALUES_KEY, true);//default is true
		final int lastSelectedCountyPosition = oikosPreferences.getInt(OikosPreferenceActivity.SELECTED_COUNTY, 0);
		if (rememberLastSelected && (countiesSpinner.getSelectedItemPosition() == lastSelectedCountyPosition) && (lastSelectedPlacePosition > -1)) {
			placesSpinner.setSelection(lastSelectedPlacePosition);
		}
	}

	private void initializeRequestButton() {
		sendRequestButton = (Button)findViewById(R.id.btn_send_request);
		sendRequestButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View view) {
				try {
					saveSelectedValues();
					//
					final Store selectedStore = (Store)storesSpinner.getSelectedItem();
					requestWrapper = new RequestWrapper(messageEditText.getText().toString(), selectedStore.getId(), selectedStore.getName());
					if (selectedStore.getId().equalsIgnoreCase(UNDEFINED_ID)) {
						requestWrapper.setStore(storeEditText.getText().toString());
					}
					final County selectedCounty = (County)countiesSpinner.getSelectedItem();
					requestWrapper.setCountyId(selectedCounty.getId());
					if (!selectedCounty.getId().equalsIgnoreCase(UNDEFINED_ID)) {
						requestWrapper.setCounty(selectedCounty.getName());
					}
					final Place selectedPlace = (Place)placesSpinner.getSelectedItem();
					requestWrapper.setPlaceId(selectedPlace.getId());
					requestWrapper.setPlace(placeEditText.getText().toString());
					requestWrapper.setStoreFacebookPageId(selectedStore.getFacebookPageId());
					//
					if (validateRequiredInputs(requestWrapper)) {
						formHandler.sendMessage(Message.obtain(formHandler, SUBMIT_PRODUCT));
					}
				} catch (final Exception e) {
					Log.i(TAG, "Server connection failed");
				}
			}
		});
	}

	private boolean validateRequiredInputs(final RequestWrapper requestWrapper) {
		if (requestWrapper.getProductName().equalsIgnoreCase("")) {
			messageUtil.textToastShort(getString(R.string.required_product_message));
			return false;
		}
		if (requestWrapper.getStoreId().equalsIgnoreCase("") && requestWrapper.getStore().equalsIgnoreCase("")) {
			messageUtil.textToastShort(getString(R.string.required_store_message));
			return false;
		}
		return true;
	}

	private List<BasicNameValuePair> getRequestValues(final RequestWrapper requestWrapper) {
		final List<BasicNameValuePair> valueList = new ArrayList<BasicNameValuePair>();
		valueList.add(new BasicNameValuePair("device", "Android " + android.os.Build.VERSION.SDK)); //required
		valueList.add(new BasicNameValuePair("productName", requestWrapper.getProductName())); //required
		valueList.add(new BasicNameValuePair("storeId", requestWrapper.getStoreId())); //required, either Id or written text
		valueList.add(new BasicNameValuePair("store", requestWrapper.getStore()));
		valueList.add(new BasicNameValuePair("countyId", requestWrapper.getCountyId()));
		valueList.add(new BasicNameValuePair("county", requestWrapper.getCounty()));
		valueList.add(new BasicNameValuePair("placeId", requestWrapper.getPlaceId()));
		valueList.add(new BasicNameValuePair("place", requestWrapper.getPlace()));
		return valueList;
	}

	/**
	 * Remember selected values
	 */
	private void saveSelectedValues() {
		final Editor editor = oikosPreferences.edit();
		editor.putInt(OikosPreferenceActivity.SELECTED_STORE, storesSpinner.getSelectedItemPosition());
		editor.putInt(OikosPreferenceActivity.SELECTED_COUNTY, countiesSpinner.getSelectedItemPosition());
		editor.putInt(OikosPreferenceActivity.SELECTED_PLACE, placesSpinner.getSelectedItemPosition());
		if (storeEditText.getVisibility() == View.VISIBLE) {
			editor.putString(OikosPreferenceActivity.SELECTED_STORE_EDITTEXT, storeEditText.getText().toString());
		} else {
			editor.putString(OikosPreferenceActivity.SELECTED_STORE_EDITTEXT, "");
		}
		if (placeEditText.getVisibility() == View.VISIBLE) {
			editor.putString(OikosPreferenceActivity.SELECTED_PLACE_EDITTEXT, placeEditText.getText().toString());
		} else {
			editor.putString(OikosPreferenceActivity.SELECTED_PLACE_EDITTEXT, "");
		}
		editor.commit();
	}

	private void initializeSelectCountyButton() {
		selectCountyBtn = (ImageButton)findViewById(R.id.select_county_btn);
		selectCountyBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View view) {
				countiesSpinner.setVisibility(View.VISIBLE);
				countiesSpinner.setSelection(0);
				countiesSpinner.performClick();
			}
		});
	}

	private void initializeSettingsAndInfoButtons() {
		preferencesBtn = (ImageButton)findViewById(R.id.preferences_btn);
		preferencesBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View view) {
				final Intent intent = new Intent(FormActivity.this, OikosPreferenceActivity.class);
				startActivity(intent);
			}
		});
		infoBtn = (ImageButton)findViewById(R.id.info_btn);
		infoBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View view) {
				final Dialog dialog = new Dialog(FormActivity.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setCancelable(true);
				dialog.setContentView(R.layout.info);
				final Button button = (Button)dialog.findViewById(R.id.aboutOk);
				button.setOnClickListener(new OnClickListener() {
					public void onClick(final View v) {
						dialog.dismiss();
					}
				});
				dialog.show();
			}
		});
	}

	private class StoresAdapter extends ArrayAdapter<Store> {
		public StoresAdapter(final Context context, final int textViewResourceId, final Store[] store) {
			super(context, textViewResourceId, store);
		}

		@Override
		public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
			final LayoutInflater inflater = getLayoutInflater();
			final View row = inflater.inflate(R.layout.store_row, parent, false);
			final TextView storeText = (TextView)row.findViewById(R.id.store_txt);
			storeText.setText(stores[position].getName());
			final ImageView icon = (ImageView)row.findViewById(R.id.icon);
			final String storeName = stores[position].getName();
			if (storeName.equalsIgnoreCase(BUNNPRIS)) {
				icon.setImageResource(R.drawable.bunnpris);
			} else if (storeName.equalsIgnoreCase(SPAR)) {
				icon.setImageResource(R.drawable.spar);
			} else if (storeName.equalsIgnoreCase(JOKER)) {
				icon.setImageResource(R.drawable.joker);
			} else if (storeName.equalsIgnoreCase(KIWI)) {
				icon.setImageResource(R.drawable.kiwi);
			} else if (storeName.equalsIgnoreCase(MENY)) {
				icon.setImageResource(R.drawable.meny);
			} else if (storeName.equalsIgnoreCase(COOP_MARKED)) {
				icon.setImageResource(R.drawable.coopmarked);
			} else if (storeName.equalsIgnoreCase(COOP_MEGA)) {
				icon.setImageResource(R.drawable.coopmega);
			} else if (storeName.equalsIgnoreCase(COOP_PRIX)) {
				icon.setImageResource(R.drawable.coopprix);
			} else if (storeName.equalsIgnoreCase(COOP_EXTRA)) {
				icon.setImageResource(R.drawable.coopextra);
			} else if (storeName.equalsIgnoreCase(COOP_OBS)) {
				icon.setImageResource(R.drawable.coopobs);
			} else if (storeName.equalsIgnoreCase(REMA_1000)) {
				icon.setImageResource(R.drawable.rema1000);
			} else if (storeName.equalsIgnoreCase(ICA_NAER)) {
				icon.setImageResource(R.drawable.icanaer);
			} else if (storeName.equalsIgnoreCase(ICA_SUPER)) {
				icon.setImageResource(R.drawable.icasuper);
			} else if (storeName.equalsIgnoreCase(ICA_MAXI)) {
				icon.setImageResource(R.drawable.icamaxi);
			}else if (storeName.equalsIgnoreCase(RIMI)) {
				icon.setImageResource(R.drawable.rimi);
			}
			return row;
		}

		/**
		 * Display the selected item.
		 */
		@Override
		public View getView(final int position, final View convertView, final ViewGroup parent) {
			final LayoutInflater inflater = getLayoutInflater();
			final View row = inflater.inflate(R.layout.store_row, parent, false);
			final ImageView icon = (ImageView)row.findViewById(R.id.icon);
			final String storeName = stores[position].getName();
			if (storeName.equalsIgnoreCase(BUNNPRIS)) {
				icon.setImageResource(R.drawable.bunnpris);
			} else if (storeName.equalsIgnoreCase(SPAR)) {
				icon.setImageResource(R.drawable.spar);
			} else if (storeName.equalsIgnoreCase(JOKER)) {
				icon.setImageResource(R.drawable.joker);
			} else if (storeName.equalsIgnoreCase(KIWI)) {
				icon.setImageResource(R.drawable.kiwi);
			} else if (storeName.equalsIgnoreCase(MENY)) {
				icon.setImageResource(R.drawable.meny);
			} else if (storeName.equalsIgnoreCase(COOP_MARKED)) {
				icon.setImageResource(R.drawable.coopmarked);
			} else if (storeName.equalsIgnoreCase(COOP_MEGA)) {
				icon.setImageResource(R.drawable.coopmega);
			} else if (storeName.equalsIgnoreCase(COOP_PRIX)) {
				icon.setImageResource(R.drawable.coopprix);
			} else if (storeName.equalsIgnoreCase(COOP_EXTRA)) {
				icon.setImageResource(R.drawable.coopextra);
			} else if (storeName.equalsIgnoreCase(COOP_OBS)) {
				icon.setImageResource(R.drawable.coopobs);
			} else if (storeName.equalsIgnoreCase(REMA_1000)) {
				icon.setImageResource(R.drawable.rema1000);
			} else if (storeName.equalsIgnoreCase(ICA_NAER)) {
				icon.setImageResource(R.drawable.icanaer);
			} else if (storeName.equalsIgnoreCase(ICA_SUPER)) {
				icon.setImageResource(R.drawable.icasuper);
			} else if (storeName.equalsIgnoreCase(ICA_SUPER)) {
				icon.setImageResource(R.drawable.icasuper);
			} else if (storeName.equalsIgnoreCase(ICA_MAXI)) {
				icon.setImageResource(R.drawable.icamaxi);
			} else {
				//If no icon found for the selected item, just display text
				icon.setVisibility(View.GONE);
				final TextView storeText = (TextView)row.findViewById(R.id.store_txt);
				storeText.setText(storeName);
			}
			return row;
		}
	}
	private class CountiesAdapter extends ArrayAdapter<County> {
		public CountiesAdapter(final Context context, final int textViewResourceId, final County[] county) {
			super(context, textViewResourceId, county);
		}

		@Override
		public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
			final LayoutInflater inflater = getLayoutInflater();
			final View row = inflater.inflate(R.layout.county_row, parent, false);
			final TextView storeText = (TextView)row.findViewById(R.id.county_txt);
			storeText.setText(counties[position].getName());
			return row;
		}

		@Override
		public View getView(final int position, final View convertView, final ViewGroup parent) {
			final LayoutInflater inflater = getLayoutInflater();
			final View row = inflater.inflate(R.layout.county_row, parent, false);
			final TextView storeText = (TextView)row.findViewById(R.id.county_txt);
			storeText.setText(counties[position].getName());
			return row;
		}
	}
	private class PlacesAdapter extends ArrayAdapter<Place> {
		public PlacesAdapter(final Context context, final int textViewResourceId, final Place[] place) {
			super(context, textViewResourceId, place);
		}

		@Override
		public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
			final LayoutInflater inflater = getLayoutInflater();
			final View row = inflater.inflate(R.layout.place_row, parent, false);
			final TextView placeText = (TextView)row.findViewById(R.id.place_txt);
			placeText.setText(places[position].getName());
			return row;
		}

		@Override
		public View getView(final int position, final View convertView, final ViewGroup parent) {
			final LayoutInflater inflater = getLayoutInflater();
			final View row = inflater.inflate(R.layout.place_row, parent, false);
			final TextView placeText = (TextView)row.findViewById(R.id.place_txt);
			placeText.setText(places[position].getName());
			return row;
		}
	}

	public void postMessageOnFacebookWall(final String facebookPageId, final String message) {
		final boolean postOnMyWall = oikosPreferences.getBoolean(OikosPreferenceActivity.POST_TO_MY_WALL_KEY, false);
		final boolean postOnStoreWall = oikosPreferences.getBoolean(OikosPreferenceActivity.POST_TO_STORE_KEY, false);
		if (postOnMyWall || postOnStoreWall) {
			if (facebookConnector.getFacebook().isSessionValid()) {
				postMessageOnFacebookWallInThread(facebookPageId, message, postOnMyWall, postOnStoreWall);
			} else {
				final SessionEvents.AuthListener listener = new SessionEvents.AuthListener() {
					public void onAuthSucceed() {
						postMessageOnFacebookWallInThread(facebookPageId, message, postOnMyWall, postOnStoreWall);
					}

					public void onAuthFail(final String error) {
						messageUtil.textToastShort(getString(R.string.facebook_login_failed));
					}
				};
				SessionEvents.addAuthListener(listener);
				facebookConnector.login();
			}
		}
	}

	private void postMessageOnFacebookWallInThread(final String facebookPageId, final String message, final boolean postOnMyWall, final boolean postOnChainWall) {
		final Thread t = new Thread() {
			@Override
			public void run() {
				try {
					facebookConnector.postMessageOnWall(facebookPageId, message, postOnMyWall, postOnChainWall);
					mFacebookHandler.post(mUpdateFacebookNotification);
				} catch (final Exception ex) {
					Log.e(TAG, "Error posting request message to facebook", ex);
				}
			}
		};
		t.start();
	}
}
