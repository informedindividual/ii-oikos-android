package com.cellobject.oikos;

import java.io.IOException;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.cellobject.oikos.model.County;
import com.cellobject.oikos.model.Store;
import com.cellobject.oikos.util.MessageUtil;
import com.cellobject.oikos.util.XmlFileParser;

public class SplashScreenActivity extends Activity {
	private OikosApplication oikosApplication;

	MessageUtil messageUtil;

	final String URL_GET_ALL_STORES = "https://informedindividual.org/oikos/m/stores/getAll/xml";

	final String URL_GET_ALL_COUNTIES = "https://informedindividual.org/oikos/m/counties/getAll/xml";

	private final int NETWORK_ERROR_MESSAGE = 1;

	Handler splashHandler = new Handler() {
		@Override
		public void handleMessage(final Message message) {
			switch (message.what) {
				case NETWORK_ERROR_MESSAGE:
					showNetworkErrorMessage();
					break;
				default:
					break;
			}
		}
	};

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		oikosApplication = (OikosApplication)this.getApplication();
		messageUtil = new MessageUtil(this);
		final XmlFileParser xmlFileParser = new XmlFileParser();
		final Thread splashThread = new Thread() {
			@Override
			public void run() {
				try {
					final String storesXml = oikosApplication.getNetwork().httpGet(URL_GET_ALL_STORES); //The server returns XML of stores
					final String countiesXml = oikosApplication.getNetwork().httpGet(URL_GET_ALL_COUNTIES); //The server returns XML of counties
					if ( (storesXml != null) && (countiesXml != null)) {
						setStoresAndCounties(xmlFileParser, storesXml, countiesXml);
						sleep(500);
						finish();
						final Intent intent = new Intent(SplashScreenActivity.this, FormActivity.class);
						startActivity(intent);
						stop();
					} else {
						splashHandler.sendMessage(Message.obtain(splashHandler, NETWORK_ERROR_MESSAGE));
					}
				} catch (final IOException e) {
					splashHandler.sendMessage(Message.obtain(splashHandler, NETWORK_ERROR_MESSAGE));
				} catch (final XmlPullParserException e) {
					splashHandler.sendMessage(Message.obtain(splashHandler, NETWORK_ERROR_MESSAGE));
				} catch (final InterruptedException e) {
					// do nothing
				}
			}
		};
		splashThread.start();
	}

	private void setStoresAndCounties(final XmlFileParser xmlFileParser, final String storesXml, final String countiesXml) throws XmlPullParserException,
	IOException {
		final List<Store> stores = xmlFileParser.parseStoresXml(storesXml);
		stores.add(0, new Store(FormActivity.UNDEFINED_ID, FormActivity.STORE_NOT_SELECTED, "", ""));//Add "Butikk, ikkke valgt" as the first item
		stores.add(stores.size(), new Store(FormActivity.UNDEFINED_ID, FormActivity.OTHER_SPECIFY, "", "")); //Add "Annen: spesifier" as the last item
		oikosApplication.setStores(stores);
		//
		final List<County> counties = xmlFileParser.parseCountiesXml(countiesXml);
		counties.add(0, new County(FormActivity.UNDEFINED_ID, FormActivity.COUNTY_NOT_SELECTED));
		oikosApplication.setCounties(counties);
	}

	private void showNetworkErrorMessage() {
		final AlertDialog dialog = new AlertDialog.Builder(SplashScreenActivity.this).create();
		dialog.setCancelable(false); // This blocks the 'BACK' button
		dialog.setMessage(getString(R.string.network_error));
		dialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.dismiss();
				SplashScreenActivity.this.finish();
			}
		});
		dialog.show();
	}
}
